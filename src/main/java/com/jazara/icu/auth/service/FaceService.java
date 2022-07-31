package com.jazara.icu.auth.service;


import com.jazara.icu.auth.domain.Cam;
import com.jazara.icu.auth.domain.Person;
import com.jazara.icu.auth.domain.User;
import com.jazara.icu.auth.payload.FaceRequest;
import com.jazara.icu.auth.payload.firebase.PushNotificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.Math.abs;

@Service
public class FaceService {

    @Autowired
    private PersonService personService;

    @Autowired
    private CamService camService;

    @Autowired
    private PushNotificationService pushNotificationService;

    public List<Person> addFaces(FaceRequest faceRequest) throws Exception {
        List<Person> addedPersons = new ArrayList<Person>();
        Optional<Cam> cam = camService.getCamById(faceRequest.getCamid());
        for (List<Double> face : faceRequest.getFaces()) {
            Person checkedPerson = checkFaceExistance(face);
            Person p = new Person();
            if (checkedPerson == null) {
                p.setAge(0);
                p.setCam(cam.get());
                p.setName("Unknown");
                p.setGender(User.Gender.Other);
                p.setOwner(null);
                p.setPhonenumber("Unknown");
                p.setFaceFeatures(face);
                personService.createPerson(p);
                addedPersons.add(p);
            } else {
                checkedPerson.setFaceFeatures(face);
                checkedPerson.setCam(cam.get());
                personService.editPerson(checkedPerson);
                personService.updateAttendanceHistory(checkedPerson);
            }
        }
        PushNotificationRequest pushNotificationRequest = new PushNotificationRequest("", "click to see", "image link", "all");
        pushNotificationRequest.setToken(faceRequest.getAppToken());
        if (faceRequest.getFall()) {
            pushNotificationRequest.setTitle("fall");
            pushNotificationService.sendPushNotificationToToken(pushNotificationRequest);
        }
        if (faceRequest.getMotion()) {
            pushNotificationRequest.setTitle("motion");
            pushNotificationService.sendPushNotificationToToken(pushNotificationRequest);
        }
        if (faceRequest.getFire()) {
            pushNotificationRequest.setTitle("Fire");
            pushNotificationService.sendPushNotificationToToken(pushNotificationRequest);
        }
        if (faceRequest.getViolance()) {
            pushNotificationRequest.setTitle("Violance");
            pushNotificationService.sendPushNotificationToToken(pushNotificationRequest);
        }
        return addedPersons;
    }

    @Transactional
    public Person checkFaceExistance(List<Double> face) {
        List<Person> persons = personService.getAllPersons();
        for (Person p : persons) {
            if (dist(p.getFaceFeatures(), face)) {
                return p;
            }
        }
        return null;
    }

    public boolean dist(List<Double> myFace, List<Double> newFace) {
        double result = 0;
        for (int i = 0; i < myFace.size(); i++) {
            double x = myFace.get(i) - newFace.get(i);
            result += abs(x);
        }
        return (result <= 0.6);
    }

}