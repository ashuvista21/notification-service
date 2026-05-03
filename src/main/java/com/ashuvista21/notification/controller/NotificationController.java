package com.ashuvista21.notification.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ashuvista21.notification.dtos.NotificationRequest;
import com.ashuvista21.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class NotificationController {
	
	private final NotificationService notificationService ;
	
	@PostMapping("/send")
    public void sendNotification(@RequestBody NotificationRequest notificationRequest) {
		
        notificationService.createAndDispatch(notificationRequest) ;
    }
}
