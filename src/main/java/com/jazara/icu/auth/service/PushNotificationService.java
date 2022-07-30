package com.jazara.icu.auth.service;

import com.jazara.icu.auth.config.DefaultsProperties;
import com.jazara.icu.auth.payload.firebase.PushNotificationRequest;
import com.jazara.icu.auth.repository.PushNotificationRepository;
import com.jazara.icu.auth.service.firebase.FCMService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class PushNotificationService {


    @Autowired
    private DefaultsProperties defaultsProperties;

    @Autowired
    PushNotificationRepository pushNotificationRepository;

    private Logger logger = LoggerFactory.getLogger(PushNotificationService.class);
    private FCMService fcmService;

    public PushNotificationService(FCMService fcmService) {
        this.fcmService = fcmService;
    }


    public void sendPushNotificationWithoutData(PushNotificationRequest request) {
        try {
            fcmService.sendMessageWithoutData(request);
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
        }
    }


    public void sendPushNotificationToToken(PushNotificationRequest request) {
        try {
            fcmService.sendMessageToToken(request);
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
        }
    }

/*    public void sendEventPushNotification(CustomEvent event, PushNotificationRequest request) {
        try {
            createOrUpdateNotification(getEventPushNotificationRequest(event, request), false);
            fcmService.sendMessage(getPayloadDataFromRequest(request), getEventPushNotificationRequest(event, request));
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
        }
    }*/

    private Map<String, String> getPayloadDataFromRequest(PushNotificationRequest request) {
        Map<String, String> pushData = new HashMap<>();
        pushData.put("click_action", !StringUtils.isEmpty(request.getClick_action()) ? request.getClick_action() : defaultsProperties.getDefaults().get("click_action"));
        pushData.put("route", !StringUtils.isEmpty(request.getRoute()) ? request.getRoute() : defaultsProperties.getDefaults().get("route"));
        return pushData;
    }

/*
    private PushNotificationRequest getEventPushNotificationRequest(CustomEvent c, PushNotificationRequest request) {
        return new PushNotificationRequest(
                !StringUtils.isEmpty(request.getTitle()) ? request.getTitle() : "We've got new Event for you !",
                !StringUtils.isEmpty(request.getBody()) ? request.getBody() : "Click to see details",
                !StringUtils.isEmpty(request.getImage()) ? request.getImage() : c.getImage(),
                !StringUtils.isEmpty(request.getTopic()) ? request.getTopic() : defaultsProperties.getDefaults().get("topic"),
                !StringUtils.isEmpty(request.getClick_action()) ? request.getClick_action() : defaultsProperties.getDefaults().get("click_action"),
                !StringUtils.isEmpty(request.getRoute()) ? request.getRoute() : defaultsProperties.getDefaults().get("route"),
                !StringUtils.isEmpty(request.getTag()) ? request.getTag() : defaultsProperties.getDefaults().get("tag"));
    }
*/

    public void sendCustomPushNotification(PushNotificationRequest request) {
        try {
            createOrUpdateNotification(request, false);
            Map<String, String> map = new HashMap<>();
            map.put("click_action", request.getClick_action());
            map.put("route", request.getRoute());
            map.put("tag", request.getTag());
            fcmService.sendMessage(map, request);
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
        }
    }

    public void sendSamplePushNotification() {
        try {
            fcmService.sendMessage(getSamplePayloadData(), getSamplePushNotificationRequest());
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
        }
    }

    private Map<String, String> getSamplePayloadData() {
        Map<String, String> pushData = new HashMap<>();
        pushData.put("evetid", defaultsProperties.getDefaults().get("eventid"));
        pushData.put("click_action", defaultsProperties.getDefaults().get("click_action"));
        pushData.put("route", defaultsProperties.getDefaults().get("route"));
        return pushData;
    }


    private PushNotificationRequest getSamplePushNotificationRequest() {
        PushNotificationRequest request = new PushNotificationRequest(
                defaultsProperties.getDefaults().get("title"),
                defaultsProperties.getDefaults().get("body"),
                defaultsProperties.getDefaults().get("image"),
                defaultsProperties.getDefaults().get("topic"));
        return request;
    }

    public List<PushNotificationRequest> getAll() {
        List<PushNotificationRequest> notifications = (List<PushNotificationRequest>) pushNotificationRepository.findAll();
        if (notifications.size() > 0) {

            return notifications;
        } else {
            return new ArrayList<PushNotificationRequest>();
        }
    }

    public PushNotificationRequest createOrUpdateNotification(PushNotificationRequest request, boolean update) {
        try {
            Optional<PushNotificationRequest> req;
            if (update) {
                req = pushNotificationRepository.findById(request.getId());
                if (req.isPresent()) {
                    PushNotificationRequest newEntity = req.get();
                    newEntity.setTitle(request.getTitle().trim());
                    newEntity.setBody(request.getBody().trim());
                    newEntity.setImage(request.getImage());
                    newEntity.setRoute(request.getRoute());
                    newEntity.setClick_action(request.getClick_action());
                    newEntity.setTopic(request.getTopic());
                    newEntity.setTag(request.getTag());
                    newEntity.setToken(request.getToken());
                    return newEntity;

                } else {
                    throw new RuntimeException("No record exist for given id " + request.getId());
                }
            } else {
                return pushNotificationRepository.save(request);
            }
        } catch (ObjectOptimisticLockingFailureException e) {
            System.err.print("Somebody has already updated the amount for item:{} in concurrent transaction.");
            throw e;
        }
    }

    public void deleteAllNotifications() {
        pushNotificationRepository.deleteAll();
    }

    @Transactional
    public void deleteNotificationById(Long id) throws RuntimeException {
        Optional<PushNotificationRequest> request = pushNotificationRepository.findById(id);
        if (request.isPresent()) {
            pushNotificationRepository.deleteById(id);
        } else {
            throw new RuntimeException("No record exist for given id");
        }
    }


}