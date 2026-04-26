package com.ashuvista21.notification.repository;

import java.util.Optional ;
import java.util.UUID ;

import org.springframework.data.jpa.repository.JpaRepository ;
import org.springframework.stereotype.Repository ;

import com.ashuvista21.notification.entities.UserNotificationTypePreference ;
import com.ashuvista21.notification.enums.NotificationType ;

@Repository
public interface UserNotificationTypePreferenceRepository extends JpaRepository<UserNotificationTypePreference, Long> {
	Optional<UserNotificationTypePreference> findByUserIdAndNotificationType(UUID userId, NotificationType notificationType) ;
}
