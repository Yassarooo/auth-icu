package com.jazara.icu.auth.controller;


import com.jazara.icu.auth.domain.Branch;
import com.jazara.icu.auth.domain.Cam;
import com.jazara.icu.auth.service.CamService;
import com.jazara.icu.auth.service.ProduceCamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequestMapping("/cam")
@RestController
public class CamController {

    @Autowired
    private CamService camService;

    @Autowired
    ProduceCamService produceCamService;

    @PostMapping(value = "/add")
    public ResponseEntity<String> createRoom(@RequestBody Cam cam) {
        final Cam c = camService.createCam(cam);
        if (c == null) {
            return new ResponseEntity<String>("cannot", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        produceCamService.produceMessage(c.getUrl());
        return new ResponseEntity<String>("success", HttpStatus.OK);
    }

    @PutMapping(value = "/edit/{id}")
    public ResponseEntity<String> editRoom(@PathVariable Long id, @RequestBody Cam cam) {
        Cam c = camService.editCam(cam);
        if (c == null) {
            return new ResponseEntity<String>("cannot", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("success", HttpStatus.OK);
    }

    @GetMapping(value = "/all/{id}")
    public ResponseEntity<?> getCamsByRoomID(@PathVariable Long id) {
        Map<String, Object> tokenMap = new HashMap<String, Object>();
        final ArrayList<Cam> cams = camService.getCamsByRoomId(id);
        tokenMap.put("cams", cams);
        return new ResponseEntity<Map<String, Object>>(tokenMap, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Optional<Cam>> getCam(@PathVariable Long id) {
        final Optional<Cam> c = camService.getCamById(id);
        if (!c.isPresent()) {
            return new ResponseEntity<Optional<Cam>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Optional<Cam>>(c, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteCam(@PathVariable Long id) {
        if (camService.deleteCamById(id))
            return new ResponseEntity<String>("success", HttpStatus.OK);
        return new ResponseEntity<String>("cannot", HttpStatus.UNAUTHORIZED);
    }
}
