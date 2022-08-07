package com.jazara.icu.auth.service;


import com.jazara.icu.auth.domain.Branch;
import com.jazara.icu.auth.domain.Department;
import com.jazara.icu.auth.repository.DepartmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class DepartmentService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private BranchService branchService;
    @Autowired
    private UserService userService;

    public Department createDepartment(Department dep) {
        Optional<Branch> b = branchService.getBranchById(dep.getBranch_id());
        if (b.isPresent()) {
            Branch temp = b.get();
            if (temp.getOwner().getId().equals(userService.getLoggedUserId()) || userService.isAdmin()) {
                dep.setBranch(temp);
                return departmentRepository.save(dep);
            }
        }
        return null;
    }

    @Transactional
    public Department editDepartment(Department dep) throws Exception {
        Optional<Branch> b = branchService.getBranchById(dep.getBranch_id());
        if (b.isPresent()) {
            Branch temp = b.get();
            if (temp.getOwner().getId().equals(userService.getLoggedUserId()) || userService.isAdmin()) {
                Optional<Department> d = departmentRepository.findById(dep.getId());
                if (!d.isPresent())
                    throw new Exception("Dep not Found");
                Department tmp = d.get();
                tmp.setName(dep.getName());
                tmp.setLocation(dep.getLocation());
                tmp.setBranch_id(temp.getId());
                tmp.setBranch(temp);
                departmentRepository.save(tmp);
                return tmp;

            }
        }
        throw new Exception("Branch not Found");
    }

    public Branch getBranchByDepId(Long id) {
        Optional<Department> d = departmentRepository.findById(id);
        if (d.isPresent()) {
            Department temp = d.get();
            if (temp.getBranch().getOwner().getId().equals(userService.getLoggedUserId()) || userService.isAdmin()) {
                return temp.getBranch();
            }
            return null;
        }
        return null;
    }

    public ArrayList<Department> getDepartmentsByBranchId(Long id) {
        Optional<Branch> b = branchService.getBranchById(id);
        if (b.isPresent()) {
            Branch temp = b.get();
            if (temp.getOwner().getId().equals(userService.getLoggedUserId()) || userService.isAdmin()) {
                return departmentRepository.findAllByBranch_id(id);
            }
        }
        return new ArrayList<Department>();
    }

    public Optional<Department> getDepartmentById(Long id) {
        Optional<Department> dep = departmentRepository.findById(id);
        if (!dep.isPresent())
            return null;
        return dep;
    }

    public Boolean deleteDepartmentById(Long id) {
        Optional<Department> d = departmentRepository.findById(id);
        if (d.isPresent()) {
            Department temp = d.get();
            if (temp.getBranch().getOwner().getId().equals(userService.getLoggedUserId()) || userService.isAdmin()) {
                departmentRepository.deleteById(id);
                return true;
            }
        }
        return false;
    }

    public void deleteAllDepartments() {
        if (userService.isAdmin())
            departmentRepository.deleteAll();
    }
}