package com.jazara.icu.auth.controller;

import com.jazara.icu.auth.domain.Department;
import com.jazara.icu.auth.service.CustomResponse;
import com.jazara.icu.auth.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequestMapping("/api/department")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class DepartmentController {

    @Autowired
    CustomResponse customResponse;

    @Autowired
    private DepartmentService departmentService;

    @PostMapping(value = "/add")
    public ResponseEntity<?> createDep(@RequestBody Department dep) {
        final Department d = departmentService.createDepartment(dep);
        if (d == null) {
            return customResponse.HandleResponse(false, "cannot add department", "", HttpStatus.OK);
        }
        return customResponse.HandleResponse(true, "", d, HttpStatus.OK);
    }

    @PutMapping(value = "/edit/{id}")
    public ResponseEntity<?> editDep(@PathVariable Long id, @RequestBody Department dep) {
        Department d = departmentService.editDepartment(dep);
        if (d == null) {
            return customResponse.HandleResponse(false, "cannot edit department", "", HttpStatus.OK);
        }
        return customResponse.HandleResponse(true, "", d, HttpStatus.OK);
    }

    @GetMapping(value = "/all/{id}")
    public ResponseEntity<?> getDepartmentsByBranchID(@PathVariable Long id) {
        Map<String, Object> tokenMap = new HashMap<String, Object>();
        final ArrayList<Department> deps = departmentService.getDepartmentsByBranchId(id);
        tokenMap.put("deps", deps);
        return customResponse.HandleResponse(true, "", tokenMap, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getDep(@PathVariable Long id) {
        final Optional<Department> d = departmentService.getDepartmentById(id);
        if (!d.isPresent()) {
            return customResponse.HandleResponse(false, "not found", "", HttpStatus.OK);
        }
        return customResponse.HandleResponse(true, "", d, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteDep(@PathVariable Long id) {
        if (departmentService.deleteDepartmentById(id))
            return customResponse.HandleResponse(true, "", "", HttpStatus.OK);
        return customResponse.HandleResponse(false, "cannot delete department", "", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/deleteAll")
    public ResponseEntity<?> DeleteAllDeps() throws Exception {
        try {
            departmentService.deleteAllDepartments();
            return customResponse.HandleResponse(true, "deleted all deps", "", HttpStatus.OK);
        } catch (Exception e) {
            return customResponse.HandleResponse(false, e.toString(), "", HttpStatus.OK);
        }
    }
}
