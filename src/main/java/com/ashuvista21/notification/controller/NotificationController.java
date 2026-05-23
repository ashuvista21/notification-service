package com.ashuvista21.notification.controller;

import java.util.UUID ;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ashuvista21.notification.dtos.NotificationCommand ;
import com.ashuvista21.notification.dtos.NotificationRequest;
import com.ashuvista21.notification.enums.NotificationType ;
import com.ashuvista21.notification.service.NotificationService;
import com.ashuvista21.notification.validator.NotificationValidator ;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class NotificationController {
	
	private final NotificationService notificationService ;
	private final NotificationValidator notificationValidator ;
	
	@PostMapping("/send")
    public void sendNotification(@RequestBody NotificationRequest notificationRequest) {
		
		notificationValidator.validate(notificationRequest) ;
		
		NotificationCommand command = new NotificationCommand(
				notificationRequest.getEventId(),
				UUID.fromString(notificationRequest.getUserId()), 
				NotificationType.valueOf(notificationRequest.getEventType()),
				notificationRequest.getVariables()) ;
		
        notificationService.createAndDispatch(command) ;
    }
}
