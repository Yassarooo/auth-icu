package com.jazara.icu.auth.service;


import com.jazara.icu.auth.domain.Person;
import com.jazara.icu.auth.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private UserService userService;

    public Person createPerson(Person person) {
        person.setOwner(userService.getUserByID(userService.getLoggedUserId()).get());
        person = personRepository.save(person);
        return person;
    }

    @Transactional
    public Person editPerson(Person person) {
        Optional<Person> p = personRepository.findById(person.getId());
        if (p.isPresent()) {
            Person temp = p.get();
            if ((temp.getOwner().getId().equals(userService.getLoggedUserId())) || userService.isAdmin()) {
                temp.setName(person.getName().trim());
                temp.setAge(person.getAge());
                temp.setGender(person.getGender());
                temp.setImageLink(person.getImageLink());
                temp.setPosition(person.getPosition());
                temp.setPhonenumber(person.getPhonenumber());
                temp.setDob(person.getDob());
                personRepository.save(temp);
                return temp;
            }
        }
        return null;
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

    public void deleteAllPersons() {
        if (userService.isAdmin())
            personRepository.deleteAll();
    }
}