package com.jazara.icu.auth.controller;

import com.jazara.icu.auth.domain.User;
import com.jazara.icu.auth.service.CustomResponse;
import com.jazara.icu.auth.service.PushNotificationService;
import com.jazara.icu.auth.config.DefaultsProperties;
import com.jazara.icu.auth.payload.firebase.PushNotificationRequest;
import com.jazara.icu.auth.payload.firebase.PushNotificationResponse;
import com.jazara.icu.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/notification")
@RestController
public class PushNotificationController {

    @Autowired
    private PushNotificationService pushNotificationService;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomResponse customResponse;

    @Autowired
    private DefaultsProperties defaultsProperties;

    public PushNotificationController(PushNotificationService pushNotificationService) {
        this.pushNotificationService = pushNotificationService;
    }

    @PostMapping("/topic")
    public ResponseEntity sendNotification(@RequestBody PushNotificationRequest request) {
        pushNotificationService.sendPushNotificationWithoutData(request);
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
    }

    @PostMapping("/token")
    public ResponseEntity sendTokenNotification(@RequestBody PushNotificationRequest request) {
        pushNotificationService.sendPushNotificationToToken(request);
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
    }

    @PostMapping("/updateToken")
    public ResponseEntity<Map<String, Object>> updateTokenForUser(@RequestHeader String usernameOrEmail, @RequestHeader String appToken, @RequestHeader String devId) {
        try {
            User u = userService.updateTokenForUser(usernameOrEmail, appToken, devId);
            return customResponse.HandleResponse(true, null, u, HttpStatus.OK);
        } catch (Exception e) {
            return customResponse.HandleResponse(false, e.getMessage(), null, HttpStatus.OK);
        }
    }


/*    @PostMapping("/notification/car")
    public ResponseEntity sendCarNotification(@RequestBody PushNotificationRequest pushRequest) {
        Car c = carService.getCarById(pushRequest.getCarid());
        if (c != null) {
            pushNotificationService.sendCarPushNotification(c, pushRequest);
            return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.NO_CONTENT.value(), "Cannot send notification. Car not found"), HttpStatus.NO_CONTENT);
        }
    }*/

    @PostMapping("/data")
    public ResponseEntity sendDataNotification(@RequestBody PushNotificationRequest pushRequest) {
        pushNotificationService.sendCustomPushNotification(pushRequest);
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity sendSampleNotification() {

        System.out.println(defaultsProperties.getDefaults().get("topic"));

        try {
            pushNotificationService.sendSamplePushNotification();
            return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/notifications", method = RequestMethod.GET)
    public List<PushNotificationRequest> getAllNotifications() {
        List<PushNotificationRequest> nots = pushNotificationService.getAll();
        return (List<PushNotificationRequest>) nots;
    }

    @RequestMapping(value = "/notifications", method = RequestMethod.DELETE)
    public ResponseEntity deleteAllNotifications() {
        pushNotificationService.deleteAllNotifications();
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/notifications", method = RequestMethod.POST)
    public ResponseEntity addNotification(@RequestBody PushNotificationRequest r) {
        pushNotificationService.createOrUpdateNotification(r, false);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/notifications", method = RequestMethod.PUT)
    public ResponseEntity editNotification(@RequestBody PushNotificationRequest r) {
        pushNotificationService.createOrUpdateNotification(r, true);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/notifications/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteNotification(@PathVariable("id") Long id) {
        pushNotificationService.deleteAllNotifications();
        return new ResponseEntity(HttpStatus.OK);
    }

}