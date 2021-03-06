package com.jazara.icu.auth.service;

import com.jazara.icu.auth.domain.Branch;
import com.jazara.icu.auth.repository.BranchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class BranchService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private UserService userService;

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
        if (id.equals(userService.getLoggedUserId()))
            return branchRepository.findAllByOwner_Id(id);
        return new ArrayList<Branch>();
    }

    public Optional<Branch> getBranchById(Long id) {
        return branchRepository.findById(id);
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

    public void deleteAllBranches() {
        if (userService.isAdmin())
            branchRepository.deleteAll();
    }
}