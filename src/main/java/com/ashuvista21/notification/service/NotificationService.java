package com.ashuvista21.notification.service;

import java.util.List ;
import java.util.UUID ;

import com.ashuvista21.notification.dtos.NotificationCommand ;
import com.ashuvista21.notification.dtos.NotificationStatusView ;

public interface NotificationService {
	void createAndDispatch(NotificationCommand command) ;
	List<NotificationStatusView> getNotificationsForUser(UUID userId, int page, int size) ;
	NotificationStatusView getNotificationById(UUID userId, UUID notificationId) ;
}
