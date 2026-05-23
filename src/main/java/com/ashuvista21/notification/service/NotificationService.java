package com.ashuvista21.notification.service;

import com.ashuvista21.notification.dtos.NotificationCommand ;

public interface NotificationService {
	void createAndDispatch(NotificationCommand command) ;
}
