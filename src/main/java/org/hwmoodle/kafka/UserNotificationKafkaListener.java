package org.hwmoodle.kafka;

import org.hwmoodle.core.dto.UserNotificationEvent;
import org.hwmoodle.core.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class UserNotificationKafkaListener {
    private static final Logger logger = LoggerFactory.getLogger(UserNotificationKafkaListener.class);
    private final NotificationService notificationService;

    public UserNotificationKafkaListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "${app.notification.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void onUserEvent(UserNotificationEvent event) {
        logger.info("Received user notification event: {}", event);
        notificationService.handleNotification(event);
    }
}
