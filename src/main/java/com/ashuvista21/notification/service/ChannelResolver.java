package com.ashuvista21.notification.service;

import java.util.Set ;
import java.util.UUID ;

import com.ashuvista21.notification.enums.NotificationChannelType ;
import com.ashuvista21.notification.enums.NotificationType ;

public interface ChannelResolver {
	Set<NotificationChannelType> resolve(UUID userId, NotificationType notificationType) ;
}
