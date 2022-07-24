package com.jazara.icu.auth.repository;


import com.jazara.icu.auth.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Repository
@Transactional
public interface PersonRepository extends JpaRepository<Person, Long> {
    ArrayList<Person> findAllByOwner_Id(Long id);
    Optional<Person> findById(Long id);

}