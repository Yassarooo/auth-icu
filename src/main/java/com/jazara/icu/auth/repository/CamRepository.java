package com.jazara.icu.auth.repository;

import com.jazara.icu.auth.domain.Cam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface CamRepository extends JpaRepository<Cam, Long> {

    Optional<Cam> findById(Long id);
    ArrayList<Cam> findAllByRoom_id(Long id);
}