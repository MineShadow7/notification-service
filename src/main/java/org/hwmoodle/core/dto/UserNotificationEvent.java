package org.hwmoodle.core.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UserNotificationEvent(
        @NotNull UserEventOperation operation,
        @Email @NotNull String email
) {
}

