package com.jazara.icu.auth.service;

import com.jazara.icu.auth.domain.Branch;
import com.jazara.icu.auth.domain.Cam;
import com.jazara.icu.auth.repository.BranchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BranchService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CamService camService;

    public Branch createBranch(Branch branch) {
        branch.setOwner(userService.getUserByID(userService.getLoggedUserId()).get());
        branch = branchRepository.save(branch);
        return branch;
    }

    @Transactional
    public Branch editBranch(Branch branch) {
        Optional<Branch> b = branchRepository.findById(branch.getId());
        if (b.isPresent()) {
            Branch temp = b.get();
            if ((temp.getOwner().getId().equals(userService.getLoggedUserId())) || userService.isAdmin()) {
                temp.setName(branch.getName().trim());
                temp.setLocation(branch.getLocation());
                branchRepository.save(temp);
                return temp;
            }
        }
        return null;
    }

    public ArrayList<Branch> getBranchesByOwnerId(Long id) {
        if (id.equals(userService.getLoggedUserId()) || userService.isAdmin())
            return branchRepository.findAllByOwner_Id(id);
        return new ArrayList<Branch>();
    }

    public Optional<Branch> getBranchById(Long id) {
        return branchRepository.findById(id);
    }

    public List<Cam> getBranchCams(Long id) throws Exception {
        Optional<Branch> b = getBranchById(id);
        if (!b.isPresent())
            throw new Exception("Branch not found");
        List<Cam> newCams = new ArrayList<Cam>();
        List<Cam> cams = camService.getAllCams();
        for (Cam cam : cams) {
            if (cam.getRoom().getDep().getBranch().getId() == b.get().getId()) {
                newCams.add(cam);
            }
        }
        return newCams;
    }

    public Boolean deleteBranchById(Long id) {
        Optional<Branch> b = branchRepository.findById(id);
        if (b.isPresent()) {
            Branch temp = b.get();
            if (temp.getOwner().getId().equals(userService.getLoggedUserId()) || userService.isAdmin()) {
                branchRepository.deleteById(id);
                return true;
            }
            return false;
        }
        return false;
    }

    public void deleteAllBranches() throws Exception {
        if (userService.isAdmin())
            branchRepository.deleteAll();
        else
            throw new Exception("UNAUTHORIZED");
    }
}