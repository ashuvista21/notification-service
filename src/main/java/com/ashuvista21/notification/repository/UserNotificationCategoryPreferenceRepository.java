package com.ashuvista21.notification.repository;

import java.util.Optional ;
import java.util.UUID ;

import org.springframework.data.jpa.repository.JpaRepository ;
import org.springframework.stereotype.Repository ;

import com.ashuvista21.notification.entities.UserNotificationCategoryPreference ;
import com.ashuvista21.notification.enums.NotificationCategory ;

@Repository
public interface UserNotificationCategoryPreferenceRepository extends JpaRepository<UserNotificationCategoryPreference, Long> {
	Optional<UserNotificationCategoryPreference> findByUserIdAndNotificationType(UUID userId, NotificationCategory notificationCategory) ;
}
