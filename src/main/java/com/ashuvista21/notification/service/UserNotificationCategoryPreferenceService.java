package com.ashuvista21.notification.service;

import java.util.List ;
import java.util.Set;
import java.util.UUID;

import com.ashuvista21.notification.dtos.UserPreferenceView ;
import com.ashuvista21.notification.enums.NotificationCategory ;
import com.ashuvista21.notification.enums.NotificationChannelType;

public interface UserNotificationCategoryPreferenceService {
	void updateUserPreferences(UUID userId, NotificationCategory notificationCategory, Set<NotificationChannelType> prefferedChannels) ;
	void addUserPreference(UUID userId, NotificationCategory notificationCategory, NotificationChannelType channel) ;
	Set<NotificationChannelType> getUserChannels(UUID userId, NotificationCategory notificationCategory) ;
	List<UserPreferenceView> getUserPreferences(UUID userId) ;
}
