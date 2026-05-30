package com.ashuvista21.notification.dtos;

public record UserPreferenceView(
		String notificationCategory,
		String notificationType,
		String[] preferredChannels) {

}
