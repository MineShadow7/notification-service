package org.hwmoodle.controller;

import jakarta.validation.Valid;
import org.hwmoodle.core.dto.UserNotificationEvent;
import org.hwmoodle.core.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/email")
    public ResponseEntity<Void> sendEmail(@Valid @RequestBody UserNotificationEvent request) {
        notificationService.handleNotification(request);
        return ResponseEntity.accepted().build();
    }
}
