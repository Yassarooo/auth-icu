package com.jazara.icu.auth.repository;

import com.jazara.icu.auth.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findById(Long id);
    ArrayList<Room> findAllByDep_id(Long id);
}