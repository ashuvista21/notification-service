package com.ashuvista21.notification.service;

import java.util.List ;
import java.util.Set;
import java.util.UUID;

import com.ashuvista21.notification.dtos.UserPreferenceView ;
import com.ashuvista21.notification.enums.NotificationChannelType;
import com.ashuvista21.notification.enums.NotificationType;

public interface UserNotificationTypePreferenceService {
	void updateUserPreferences(UUID userId, NotificationType notificationType, Set<NotificationChannelType> prefferedChannels) ;
	void addUserPreference(UUID userId, NotificationType notificationType, NotificationChannelType channel) ;
	Set<NotificationChannelType> getUserChannels(UUID userId, NotificationType notificationType) ;
	List<UserPreferenceView> getUserPreferences(UUID userId) ;
}
