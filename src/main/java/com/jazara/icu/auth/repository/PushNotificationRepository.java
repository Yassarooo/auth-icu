package com.jazara.icu.auth.repository;

import com.jazara.icu.auth.payload.PushNotificationRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface PushNotificationRepository extends CrudRepository<PushNotificationRequest, Long> {
    PushNotificationRequest findByCarid(String carid);
}