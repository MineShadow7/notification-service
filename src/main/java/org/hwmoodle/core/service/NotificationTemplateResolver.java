package org.hwmoodle.core.service;

import org.hwmoodle.core.dto.UserEventOperation;
import org.springframework.stereotype.Component;

@Component
public class NotificationTemplateResolver {
    private static final String CREATED_SUBJECT = "Аккаунт успешно создан";
    private static final String CREATED_BODY = "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.";
    private static final String DELETED_SUBJECT = "Аккаунт удалён";
    private static final String DELETED_BODY = "Здравствуйте! Ваш аккаунт был удалён.";

    public String resolveSubject(UserEventOperation operation) {
        return operation == UserEventOperation.CREATED ? CREATED_SUBJECT : DELETED_SUBJECT;
    }

    public String resolveBody(UserEventOperation operation) {
        return operation == UserEventOperation.CREATED ? CREATED_BODY : DELETED_BODY;
    }
}

