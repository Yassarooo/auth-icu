package com.jazara.icu.auth.controller;

import com.jazara.icu.auth.domain.Person;
import com.jazara.icu.auth.service.PersonService;
import com.jazara.icu.auth.service.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequestMapping("/api/person")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class PersonController {

    @Autowired
    CustomResponse customResponse;

    @Autowired
    private PersonService personService;

    @PostMapping(value = "/add")
    public ResponseEntity<Map<String, Object>> createPerson(@RequestBody Person person) {
        final Person b = personService.createPerson(person);
        if (b == null) {
            return customResponse.HandleResponse(false, "error creating person", "", HttpStatus.OK);
        }
        return customResponse.HandleResponse(true, "", b, HttpStatus.OK);
    }

    @PutMapping(value = "/edit/{id}")
    public ResponseEntity<Map<String, Object>> editPerson(@PathVariable Long id, @RequestBody Person person) {
        Person b = personService.editPerson(person);
        if (b == null) {
            return customResponse.HandleResponse(false, "error updating person info", "", HttpStatus.OK);
        }
        return customResponse.HandleResponse(true, "", b, HttpStatus.OK);
    }

    @GetMapping(value = "/user/{id}")
    public ResponseEntity<Map<String, Object>> getPersonsByOwnerId(@PathVariable Long id) throws Exception {
        try {
            Map<String, Object> tokenMap = new HashMap<String, Object>();
            final ArrayList<Person> persons = personService.getPersonsByOwnerId(id);
            tokenMap.put("persons", persons);
            return customResponse.HandleResponse(true, "", tokenMap, HttpStatus.OK);
        } catch (Exception e) {
            return customResponse.HandleResponse(false, e.toString(), "", HttpStatus.OK);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Map<String, Object>> getPerson(@PathVariable Long id) {
        final Optional<Person> b = personService.getPersonById(id);
        if (!b.isPresent()) {
            return customResponse.HandleResponse(false, "person not found", "", HttpStatus.OK);
        }
        return customResponse.HandleResponse(true, "", b, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Map<String, Object>> deletePerson(@PathVariable Long id) {
        if (personService.deletePersonById(id))
            return customResponse.HandleResponse(true, "", "", HttpStatus.OK);
        return customResponse.HandleResponse(false, "cannot delete person", "", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/deleteAll")
    public ResponseEntity<?> DeleteAllPersons() throws Exception {
        try {
            personService.deleteAllPersons();
            return customResponse.HandleResponse(true, "deleted all persons", "", HttpStatus.OK);
        } catch (Exception e) {
            return customResponse.HandleResponse(false, e.toString(), "", HttpStatus.OK);
        }
    }
}
