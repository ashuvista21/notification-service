package com.ashuvista21.notification.service;

import java.util.Set;
import java.util.UUID;

import com.ashuvista21.notification.enums.NotificationChannelType;
import com.ashuvista21.notification.enums.NotificationType;

public interface UserNotificationTypePreferenceService {
	void updateUserPreferences(UUID userId, NotificationType notificationType, Set<NotificationChannelType> prefferedChannels) ;
	void addUserPreference(UUID userId, NotificationType notificationType, NotificationChannelType channel) ;
	Set<NotificationChannelType> getUserChannels(UUID userId, NotificationType notificationType) ;
}
