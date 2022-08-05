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

import java.util.*;

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
            return customResponse.HandleResponse(true, null, c, HttpStatus.OK);
        } catch (Exception e) {
            return customResponse.HandleResponse(false, e.getMessage(), null, HttpStatus.OK);
        }
    }

    @PutMapping(value = "/edit/{id}")
    public ResponseEntity<Map<String, Object>> editCam(@PathVariable Long id, @RequestBody Cam cam) {
        try {
            Cam c = camService.editCam(cam);
            return customResponse.HandleResponse(true, null, c, HttpStatus.OK);
        } catch (Exception e) {
            return customResponse.HandleResponse(false, e.getMessage(), null, HttpStatus.OK);
        }
    }

    @GetMapping(value = "/all/{id}")
    public ResponseEntity<?> getCamsByRoomID(@PathVariable Long id) {
        try {
            Map<String, Object> camMap = new HashMap<String, Object>();
            final ArrayList<Cam> cams = camService.getCamsByRoomId(id);
            camMap.put("cams", cams);
            return customResponse.HandleResponse(true, null, camMap, HttpStatus.OK);
        } catch (Exception e) {
            return customResponse.HandleResponse(false, e.getMessage(), null, HttpStatus.OK);
        }

    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Map<String, Object>> getCam(@PathVariable Long id) {
        try {
            final Optional<Cam> c = camService.getCamById(id);
            return customResponse.HandleResponse(true, null, c, HttpStatus.OK);
        } catch (Exception e) {
            return customResponse.HandleResponse(false, e.getMessage(), null, HttpStatus.OK);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Map<String, Object>> deleteCam(@PathVariable Long id) {
        try {
            camService.deleteCamById(id);
            return customResponse.HandleResponse(true, null, null, HttpStatus.OK);
        } catch (Exception e) {
            return customResponse.HandleResponse(false, e.getMessage(), null, HttpStatus.OK);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/deleteAll")
    public ResponseEntity<?> DeleteAllCams() {
        try {
            camService.deleteAllCams();
            return customResponse.HandleResponse(true, "deleted all cams", null, HttpStatus.OK);
        } catch (Exception e) {
            return customResponse.HandleResponse(false, e.getMessage(), null, HttpStatus.OK);
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> GetAllCams() throws Exception {
        try {
            List<Cam> camList = camService.getAllCams();
            return customResponse.HandleResponse(true, null, camList, HttpStatus.OK);
        } catch (Exception e) {
            return customResponse.HandleResponse(false, e.getMessage(), null, HttpStatus.OK);
        }
    }
}
