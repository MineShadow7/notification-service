package org.hwmoodle.core.repository;

import org.hwmoodle.core.model.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
}

