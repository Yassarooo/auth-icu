package com.jazara.icu.auth.controller;

import com.jazara.icu.auth.domain.Person;
import com.jazara.icu.auth.payload.FrameRequest;
import com.jazara.icu.auth.service.AiService;
import com.jazara.icu.auth.service.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequestMapping("/api/ai")
@RestController
public class FaceRecognitionController {

    @Autowired
    CustomResponse customResponse;

    @Autowired
    private AiService aiService;

    @PostMapping(value = "/newfaces")
    public ResponseEntity<Map<String, Object>> addFrameRequest(@RequestBody FrameRequest frameRequest) throws Exception {
        try {
            return customResponse.HandleResponse(true, null, aiService.processFrame(frameRequest), HttpStatus.OK);
        } catch (Exception e) {
            return customResponse.HandleResponse(false, e.getMessage(), null, HttpStatus.OK);

        }
    }

    @PostMapping(value = "/search")
    public ResponseEntity<Map<String, Object>> SearchByFace(@RequestBody List<Double> face) throws Exception {
        try {
            Person p = aiService.checkFaceExistance(face);
            if (p != null)
                return customResponse.HandleResponse(true, null, p, HttpStatus.OK);
            else
                return customResponse.HandleResponse(false, "face not found", null, HttpStatus.OK);
        } catch (Exception e) {
            return customResponse.HandleResponse(false, e.getMessage(), null, HttpStatus.OK);

        }
    }
}
