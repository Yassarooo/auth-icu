package com.jazara.icu.auth.service;


import com.jazara.icu.auth.domain.Person;
import com.jazara.icu.auth.domain.User;
import com.jazara.icu.auth.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private UserService userService;

    public Person createPerson(Person person) {
        person.setOwner(userService.getUserByID(userService.getLoggedUserId()).get());
        person.setDetectedBycamId(0L);
        return personRepository.save(person);
    }

    @Transactional
    public Person editPerson(Person person) throws Exception {
        Optional<Person> p = personRepository.findById(person.getId());
        if (p.isPresent()) {
            Person temp = p.get();
            temp.setName(person.getName().trim());
            temp.setAge(person.getAge());
            temp.setGender(person.getGender());
            temp.setPosition(person.getPosition());
            temp.setPhonenumber(person.getPhonenumber());
            temp.setDob(person.getDob());
            temp.setDetectedBycamId(person.getDetectedBycamId());
            temp.setFaceFeatures(person.getFaceFeatures());
            temp.setAttendancehistory(person.getAttendancehistory());
            personRepository.save(temp);
            return temp;
        }
        throw new Exception("person not found ");
    }

    @Transactional
    public void updateAttendanceHistory(Person person) throws Exception {
        Optional<Person> p = personRepository.findById(person.getId());
        if (p.isPresent()) {
            Person temp = p.get();
            Date now = new Date();
            Map<Date, Boolean> history = temp.getAttendancehistory();
            history.put(now, true);
            personRepository.save(temp);
        } else
            throw new Exception("person not found ");
    }


    public ArrayList<Person> getPersonsByOwnerId(Long id) throws Exception {
        if (id.equals(userService.getLoggedUserId()) || userService.isAdmin())
            return personRepository.findAllByOwner_Id(id);
        throw new Exception("UNAUTHORIZED");
    }

    public Optional<Person> getPersonById(Long id) {
        return personRepository.findById(id);
    }

    public Boolean deletePersonById(Long id) {
        Optional<Person> p = personRepository.findById(id);
        if (p.isPresent()) {
            Person temp = p.get();
            if (temp.getOwner().getId().equals(userService.getLoggedUserId()) || userService.isAdmin()) {
                personRepository.deleteById(id);
                return true;
            }
            return false;
        }
        return false;
    }

    public List<Person> getAllPersons() {
        List<Person> personsList = (List<Person>) personRepository.findAll();

        if (personsList.size() > 0) {
            return personsList;
        } else {
            return new ArrayList<Person>();
        }
    }


    public void deleteAllPersons() {
        if (userService.isAdmin())
            personRepository.deleteAll();
    }
}