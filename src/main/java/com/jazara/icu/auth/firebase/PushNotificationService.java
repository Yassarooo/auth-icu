package com.jazara.icu.auth.firebase;

import com.jazara.icu.auth.payload.DefaultsProperties;
import com.jazara.icu.auth.payload.PushNotificationRequest;
import com.jazara.icu.auth.repository.PushNotificationRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
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

/*    public void sendCarPushNotification(Car c, PushNotificationRequest request) {
        try {
            createOrUpdateNotification(getCarPushNotificationRequest(c, request), false);
            fcmService.sendMessage(getPayloadDataFromRequest(request), getCarPushNotificationRequest(c, request));
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
        }
    }*/

    private Map<String, String> getPayloadDataFromRequest(PushNotificationRequest request) {
        Map<String, String> pushData = new HashMap<>();
        pushData.put("carid", request.getCarid().toString());
        pushData.put("click_action", !StringUtils.isEmpty(request.getClick_action()) ? request.getClick_action() : defaultsProperties.getDefaults().get("click_action"));
        pushData.put("route", !StringUtils.isEmpty(request.getRoute()) ? request.getRoute() : defaultsProperties.getDefaults().get("route"));
        return pushData;
    }

/*    private PushNotificationRequest getCarPushNotificationRequest(Car c, PushNotificationRequest request) {
        return new PushNotificationRequest(
                request.getCarid(),
                !StringUtils.isEmpty(request.getTitle()) ? request.getTitle() : "We've got new car for you !",
                !StringUtils.isEmpty(request.getBody()) ? request.getBody() : "The new " + c.getBrand() + " " + c.getModel() + " " + c.getYear() + " is now here! Click to see details",
                !StringUtils.isEmpty(request.getImage()) ? request.getImage() : c.getBrandlogo(),
                !StringUtils.isEmpty(request.getTopic()) ? request.getTopic() : defaults.get("topic"),
                !StringUtils.isEmpty(request.getClick_action()) ? request.getClick_action() : defaults.get("click_action"),
                !StringUtils.isEmpty(request.getRoute()) ? request.getRoute() : defaults.get("route"),
                !StringUtils.isEmpty(request.getTag()) ? request.getTag() : defaults.get("tag"));
    }*/

    public void sendCustomPushNotification(PushNotificationRequest request) {
        try {
            createOrUpdateNotification(request, false);
            Map<String, String> map = new HashMap<>();
            map.put("carid", request.getCarid().toString());
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
        pushData.put("carid", defaultsProperties.getDefaults().get("carid"));
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
                    newEntity.setCarid(request.getCarid());
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