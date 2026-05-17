package com.ashuvista21.notification.service;

import com.ashuvista21.notification.dtos.NotificationRequest;

public interface NotificationService {
	void createAndDispatch(NotificationRequest request) ;
}
