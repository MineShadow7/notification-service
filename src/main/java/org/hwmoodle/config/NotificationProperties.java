package org.hwmoodle.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.notification")
public record NotificationProperties(
        String senderEmail,
        String topic
) {
}

