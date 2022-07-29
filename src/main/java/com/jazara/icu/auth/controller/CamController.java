package com.jazara.icu.auth.controller;


import com.jazara.icu.auth.domain.Cam;
import com.jazara.icu.auth.service.CamService;
import com.jazara.icu.auth.service.CustomResponse;
import com.jazara.icu.auth.service.ProduceCamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequestMapping("/api/cam")
@RestController
public class CamController {

    @Autowired
    private CamService camService;

    @Autowired
    ProduceCamService produceCamService;

    @Autowired
    CustomResponse customResponse;

    @PostMapping(value = "/add")
    public ResponseEntity<Map<String, Object>> createCam(@RequestBody Cam cam) {
        try {
            final Cam c = camService.createCam(cam);
            produceCamService.produceMessage(c.getUrl());
            return customResponse.HandleResponse(true, "", c, HttpStatus.OK);
        } catch (Exception e) {
            return customResponse.HandleResponse(false, e.getMessage(), "", HttpStatus.OK);
        }
    }

    @PutMapping(value = "/edit/{id}")
    public ResponseEntity<Map<String, Object>> editCam(@PathVariable Long id, @RequestBody Cam cam) {
        try {
            Cam c = camService.editCam(cam);
            return customResponse.HandleResponse(true, "", c, HttpStatus.OK);
        } catch (Exception e) {
            return customResponse.HandleResponse(false, e.getMessage(), "", HttpStatus.OK);
        }
    }

    @GetMapping(value = "/all/{id}")
    public ResponseEntity<?> getCamsByRoomID(@PathVariable Long id) {
        try {
            Map<String, Object> camMap = new HashMap<String, Object>();
            final ArrayList<Cam> cams = camService.getCamsByRoomId(id);
            camMap.put("cams", cams);
            return customResponse.HandleResponse(true, "", camMap, HttpStatus.OK);
        } catch (Exception e) {
            return customResponse.HandleResponse(false, e.getMessage(), "", HttpStatus.OK);
        }

    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Map<String, Object>> getCam(@PathVariable Long id) {
        try {
            final Optional<Cam> c = camService.getCamById(id);
            return customResponse.HandleResponse(true, "", c, HttpStatus.OK);
        } catch (Exception e) {
            return customResponse.HandleResponse(false, e.getMessage(), "", HttpStatus.OK);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Map<String, Object>> deleteCam(@PathVariable Long id) {
        try {
            camService.deleteCamById(id);
            return customResponse.HandleResponse(true, "", "", HttpStatus.OK);
        } catch (Exception e) {
            return customResponse.HandleResponse(false, e.getMessage(), "", HttpStatus.OK);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/deleteAll")
    public ResponseEntity<?> DeleteAllCams() {
        try {
            camService.deleteAllCams();
            return customResponse.HandleResponse(true, "deleted all cams", "", HttpStatus.OK);
        } catch (Exception e) {
            return customResponse.HandleResponse(false, e.getMessage(), "", HttpStatus.OK);
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> GetAllCams() throws Exception {
        try {
            return customResponse.HandleResponse(true, "", camService.getAllCams(), HttpStatus.OK);
        } catch (Exception e) {
            return customResponse.HandleResponse(false, e.getMessage(), "", HttpStatus.OK);
        }
    }
}
