package com.jazara.icu.auth.service;

import brave.internal.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CustomResponse {

    public ResponseEntity<Map<String, Object>> HandleResponse(Boolean success, @Nullable String message, @Nullable Object result, HttpStatus status) {
        Map<String, Object> tokenMap = new HashMap<String, Object>();
        tokenMap.put("success", success);
        tokenMap.put("message", message);
        tokenMap.put("result", result);
        return new ResponseEntity<Map<String, Object>>(tokenMap, status);
    }
}
