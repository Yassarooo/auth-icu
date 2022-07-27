package com.jazara.icu.auth.controller;


import com.jazara.icu.auth.domain.Department;
import com.jazara.icu.auth.domain.Room;
import com.jazara.icu.auth.service.CustomResponse;
import com.jazara.icu.auth.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequestMapping("/api/room")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class RoomController {

    @Autowired
    private CustomResponse customResponse;
    @Autowired
    private RoomService roomService;

    @PostMapping(value = "/add")
    public ResponseEntity<?> createRoom(@RequestBody Room room) {
        final Room r = roomService.createRoom(room);
        if (r == null) {
            return customResponse.HandleResponse(false, "cannot add room", "", HttpStatus.OK);
        }
        return customResponse.HandleResponse(true, "", r, HttpStatus.OK);
    }

    @PutMapping(value = "/edit/{id}")
    public ResponseEntity<?> editRoom(@PathVariable Long id, @RequestBody Room room) {
        Room r = roomService.editRoom(room);
        if (r == null) {
            return customResponse.HandleResponse(false, "cannot edit room", "", HttpStatus.OK);
        }
        return customResponse.HandleResponse(true, "", r, HttpStatus.OK);
    }

    @GetMapping(value = "/all/{id}")
    public ResponseEntity<?> getRoomsByDepID(@PathVariable Long id) {
        Map<String, Object> tokenMap = new HashMap<String, Object>();
        final ArrayList<Room> rooms = roomService.getRoomsByDepId(id);
        tokenMap.put("rooms", rooms);
        return customResponse.HandleResponse(true, "", tokenMap, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getRoom(@PathVariable Long id) {
        final Optional<Room> r = roomService.getRoomById(id);
        if (!r.isPresent()) {
            return customResponse.HandleResponse(false, "not found", "", HttpStatus.OK);
        }
        return customResponse.HandleResponse(true, "", r, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable Long id) {
        if (roomService.deleteRoomById(id))
            return customResponse.HandleResponse(true, "", "", HttpStatus.OK);
        return customResponse.HandleResponse(false, "cannot delete room", "", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/deleteAll")
    public ResponseEntity<?> DeleteAllRooms() throws Exception {
        try {
            roomService.deleteAllRooms();
            return customResponse.HandleResponse(true, "deleted all rooms", "", HttpStatus.OK);
        } catch (Exception e) {
            return customResponse.HandleResponse(false, e.toString(), "", HttpStatus.OK);
        }
    }
}
