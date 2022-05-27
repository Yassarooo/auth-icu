package com.jazara.icu.auth.controller;

import com.jazara.icu.auth.domain.Department;
import com.jazara.icu.auth.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequestMapping("/department")
@RestController
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping(value = "/add")
    public ResponseEntity<String> createDep(@RequestBody Department dep) {
        final Department d = departmentService.createDepartment(dep);
        if (d == null) {
            return new ResponseEntity<String>("cannot", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>("success", HttpStatus.OK);
    }

    @PutMapping(value = "/edit/{id}")
    public ResponseEntity<String> editDep(@PathVariable Long id, @RequestBody Department dep) {
        Department d = departmentService.editDepartment(dep);
        if (d == null) {
            return new ResponseEntity<String>("cannot", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("success", HttpStatus.OK);
    }

    @GetMapping(value = "/all/{id}")
    public ResponseEntity<?> getDepartmentsByBranchID(@PathVariable Long id) {
        Map<String, Object> tokenMap = new HashMap<String, Object>();
        final ArrayList<Department> deps = departmentService.getDepartmentsByBranchId(id);
        tokenMap.put("deps", deps);
        return new ResponseEntity<Map<String, Object>>(tokenMap, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Optional<Department>> getDep(@PathVariable Long id) {
        final Optional<Department> d = departmentService.getDepartmentById(id);
        if (!d.isPresent()) {
            return new ResponseEntity<Optional<Department>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Optional<Department>>(d, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteDep(@PathVariable Long id) {
        if (departmentService.deleteDepartmentById(id))
            return new ResponseEntity<String>("success", HttpStatus.OK);
        return new ResponseEntity<String>("cannot", HttpStatus.UNAUTHORIZED);
    }
}
