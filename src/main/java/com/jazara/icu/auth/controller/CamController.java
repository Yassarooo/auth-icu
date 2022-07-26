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
    public ResponseEntity<Map<String, Object>> createRoom(@RequestBody Cam cam) {
        final Cam c = camService.createCam(cam);
        if (c == null) {
            return customResponse.HandleResponse(false, "cannot add cam", "", HttpStatus.OK);
        }
        produceCamService.produceMessage(c.getUrl());
        return customResponse.HandleResponse(true, "", c, HttpStatus.OK);
    }

    @PutMapping(value = "/edit/{id}")
    public ResponseEntity<Map<String, Object>> editRoom(@PathVariable Long id, @RequestBody Cam cam) {
        Cam c = camService.editCam(cam);
        if (c == null) {
            return customResponse.HandleResponse(false, "cannot edit cam", "", HttpStatus.OK);
        }
        return customResponse.HandleResponse(true, "", c, HttpStatus.OK);
    }

    @GetMapping(value = "/all/{id}")
    public ResponseEntity<?> getCamsByRoomID(@PathVariable Long id) {
        Map<String, Object> camMap = new HashMap<String, Object>();
        final ArrayList<Cam> cams = camService.getCamsByRoomId(id);
        camMap.put("cams", cams);
        return customResponse.HandleResponse(true, "", camMap, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Map<String, Object>> getCam(@PathVariable Long id) {
        final Optional<Cam> c = camService.getCamById(id);
        if (!c.isPresent()) {
            return customResponse.HandleResponse(false, "cam not found", "", HttpStatus.OK);
        }
        return customResponse.HandleResponse(true, "", c, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Map<String, Object>> deleteCam(@PathVariable Long id) {
        if (camService.deleteCamById(id))
            return customResponse.HandleResponse(true, "", "", HttpStatus.OK);
        return customResponse.HandleResponse(true, "cannot delete cam", "", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/deleteAll")
    public ResponseEntity<?> DeleteAllCams() throws Exception {
        try {
            camService.deleteAllCams();
            return customResponse.HandleResponse(true, "deleted all cams", "", HttpStatus.OK);
        } catch (Exception e) {
            return customResponse.HandleResponse(false, e.toString(), "", HttpStatus.OK);
        }
    }
}
