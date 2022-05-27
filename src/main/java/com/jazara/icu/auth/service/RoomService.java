package com.jazara.icu.auth.service;

import com.jazara.icu.auth.domain.Branch;
import com.jazara.icu.auth.domain.Department;
import com.jazara.icu.auth.domain.Room;
import com.jazara.icu.auth.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class RoomService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private UserService userService;

    public Room createRoom(Room room) {
        Branch b = departmentService.getBranchByDepId(room.getDep_id());
        if (b != null && (b.getOwner().getId().equals(userService.getLoggedUserId()) || userService.isAdmin())) {
            return roomRepository.save(room);
        }
        return null;
    }

    @Transactional
    public Room editRoom(Room room) {
        Branch b = departmentService.getBranchByDepId(room.getDep_id());
        if (b != null && (b.getOwner().getId().equals(userService.getLoggedUserId()) || userService.isAdmin())) {
            Optional<Room> r = roomRepository.findById(room.getId());
            if (!r.isPresent())
                return null;
            Room temp = r.get();
            temp.setName(room.getName());
            roomRepository.save(temp);
            return temp;
        }
        return null;
    }


    public Department getDepByRoomId(Long id) {
        Optional<Room> r = roomRepository.findById(id);
        if (r.isPresent()) {
            Optional<Department> d = departmentService.getDepartmentById(r.get().getDep_id());
            return d.get();
        }
        LOGGER.info("null");
        return null;
    }

    public ArrayList<Room> getRoomsByDepId(Long id) {
        Optional<Department> d = departmentService.getDepartmentById(id);
        if (d.isPresent()) {
            Department temp = d.get();
            if (temp.getBranch().getOwner().getId().equals(userService.getLoggedUserId()) || userService.isAdmin()) {
                return roomRepository.findAllByDep_id(id);
            }
        }
        return new ArrayList<Room>();
    }

    public Optional<Room> getRoomById(Long id) {
        Optional<Room> room = roomRepository.findById(id);
        if (!room.isPresent()) {
            return null;
        }
        return room;
    }

    public Boolean deleteRoomById(Long id) {
        Optional<Room> r = roomRepository.findById(id);
        if (r.isPresent()) {
            Room temp = r.get();
            if (temp.getDep().getBranch().getOwner().getId().equals(userService.getLoggedUserId()) || userService.isAdmin()) {
                roomRepository.deleteById(id);
                return true;
            }
        }
        return false;
    }

    public void deleteAllRooms() {
        if (userService.isAdmin())
            roomRepository.deleteAll();
    }
}