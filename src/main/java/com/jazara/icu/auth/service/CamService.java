package com.jazara.icu.auth.service;

import com.jazara.icu.auth.domain.Cam;
import com.jazara.icu.auth.domain.Department;
import com.jazara.icu.auth.domain.Room;
import com.jazara.icu.auth.repository.CamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class CamService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private CamRepository camRepository;
    @Autowired
    private RoomService roomService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private BranchService branchService;
    @Autowired
    private UserService userService;

    public Cam createCam(Cam cam) {
        Optional<Room> r = roomService.getRoomById(cam.getRoom_id());
        if (!r.isPresent())
            return null;
        Department d = roomService.getDepByRoomId(cam.getRoom_id());
        if (d == null)
            return null;
        if (d.getBranch() != null && (d.getBranch().getOwner().getId().equals(userService.getLoggedUserId()) || userService.isAdmin())) {
            cam.setRoom(r.get());
            return camRepository.save(cam);
        }
        return null;
    }

    @Transactional
    public Cam editCam(Cam cam) {
        Optional<Room> r = roomService.getRoomById(cam.getRoom_id());
        if (!r.isPresent())
            return null;
        Department d = roomService.getDepByRoomId(cam.getRoom_id());
        if (d == null)
            return null;
        if (d.getBranch() != null && d.getBranch().getOwner().getId().equals(userService.getLoggedUserId()) || userService.isAdmin()) {
            Optional<Cam> c = camRepository.findById(cam.getId());
            if (!c.isPresent())
                return null;
            Cam temp = c.get();
            temp.setName(cam.getName());
            temp.setUrl(cam.getUrl());
            temp.setRoom_id(cam.getRoom_id());
            temp.setRoom(r.get());
            camRepository.save(temp);
            return temp;
        }
        return null;
    }

    public ArrayList<Cam> getCamsByRoomId(Long id) {
        Optional<Room> r = roomService.getRoomById(id);
        if (!r.isPresent())
            return null;
        Department d = roomService.getDepByRoomId(id);
        if (d == null)
            return null;
        if (d.getBranch() != null && (d.getBranch().getOwner().getId().equals(userService.getLoggedUserId()) || userService.isAdmin())) {
            return camRepository.findAllByRoom_id(id);
        }
        return new ArrayList<Cam>();
    }

    public Optional<Cam> getCamById(Long id) {
        Optional<Cam> c = camRepository.findById(id);
        if (!c.isPresent()) {
            return null;
        }
        return c;
    }

    public Boolean deleteCamById(Long id) {
        Optional<Cam> c = camRepository.findById(id);
        if (c.isPresent()) {
            Cam temp = c.get();
            if (temp.getRoom().getDep().getBranch().getOwner().getId().equals(userService.getLoggedUserId()) || userService.isAdmin()) {
                camRepository.deleteById(id);
                return true;
            }
        }
        return false;
    }

    public void deleteAllCams() {
        if (userService.isAdmin())
            camRepository.deleteAll();
    }
}