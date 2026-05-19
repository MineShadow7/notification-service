package org.hwmoodle.core.service;

import org.hwmoodle.core.dto.UserNotificationEvent;
import org.hwmoodle.core.model.NotificationEntity;
import org.hwmoodle.core.repository.NotificationRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationTemplateResolver templateResolver;
    private final EmailNotificationService emailNotificationService;

    public NotificationService(NotificationRepository notificationRepository,
                               NotificationTemplateResolver templateResolver,
                               EmailNotificationService emailNotificationService) {
        this.notificationRepository = notificationRepository;
        this.templateResolver = templateResolver;
        this.emailNotificationService = emailNotificationService;
    }

    public void handleNotification(UserNotificationEvent event) {
        String subject = templateResolver.resolveSubject(event.operation());
        String body = templateResolver.resolveBody(event.operation());
        NotificationEntity entity = new NotificationEntity(event.email(), event.operation(), subject, body);
        notificationRepository.save(entity);
        emailNotificationService.sendNotification(event, subject, body);
    }
}

