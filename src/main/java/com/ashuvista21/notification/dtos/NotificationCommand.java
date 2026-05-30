package com.ashuvista21.notification.dtos;

import java.util.Map ;
import java.util.UUID ;

import com.ashuvista21.notification.enums.NotificationType ;

public record NotificationCommand(
		String userEventRef,
		UUID userId,
        NotificationType notificationType,
        Map<String, String> variables) {
}
