package org.hwmoodle.core.service;

import org.hwmoodle.config.NotificationProperties;
import org.hwmoodle.core.dto.UserNotificationEvent;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {
    private final JavaMailSender mailSender;
    private final NotificationProperties notificationProperties;
    private final NotificationTemplateResolver templateResolver;

    public EmailNotificationService(JavaMailSender mailSender,
                                    NotificationProperties notificationProperties,
                                    NotificationTemplateResolver templateResolver) {
        this.mailSender = mailSender;
        this.notificationProperties = notificationProperties;
        this.templateResolver = templateResolver;
    }

    public void sendNotification(UserNotificationEvent event, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(notificationProperties.senderEmail());
        message.setTo(event.email());
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
