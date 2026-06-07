package org.hwmoodle.core.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.hwmoodle.config.NotificationProperties;
import org.hwmoodle.core.dto.UserNotificationEvent;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);

    private final JavaMailSender mailSender;
    private final NotificationProperties notificationProperties;

    public EmailNotificationService(JavaMailSender mailSender,
                                    NotificationProperties notificationProperties) {
        this.mailSender = mailSender;
        this.notificationProperties = notificationProperties;
    }

    @CircuitBreaker(name = "emailServiceCB", fallbackMethod = "sendNotificationFallback")
    public void sendNotification(UserNotificationEvent event, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(notificationProperties.senderEmail());
        message.setTo(event.email());
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public void sendNotificationFallback(UserNotificationEvent event, String subject, String body, Throwable throwable) {
        logger.warn("Email sending failed for {}. Notification will be stored only. Cause: {}",
                event.email(), throwable.getMessage());
    }
}
