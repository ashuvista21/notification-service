package com.ashuvista21.notification.service.impl;

import java.util.ArrayList ;

import org.springframework.stereotype.Service;

import com.ashuvista21.notification.dispatcher.NotificationDispatcher;
import com.ashuvista21.notification.dtos.NotificationRequest;
import com.ashuvista21.notification.entities.Notification;
import com.ashuvista21.notification.enums.NotificationStatus ;
import com.ashuvista21.notification.enums.NotificationType ;
import com.ashuvista21.notification.service.NotificationService;
import com.github.f4b6a3.uuid.UuidCreator ;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {
	
	private final NotificationDispatcher dispatcher ;

	@Override
	public void process(NotificationRequest request) {
		NotificationType notificationType = NotificationType.valueOf(request.getEventType()) ;
		
		Notification notification = Notification.builder()
				.id(UuidCreator.getTimeOrdered())
				.userId(request.getUserId())
				.notificationType(notificationType)
				.category(notificationType.getCategory())
				.status(NotificationStatus.CREATED)
				.payload(request.getVariables().toString())
				.channels(new ArrayList<>())
				.build() ;
		
		dispatcher.dispatch(notification) ;
	}

}
