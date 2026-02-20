package com.ashuvista21.notification.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {
	
	@PostMapping("/send")
	public String sendNotification() {
		return "Notification sent successfully!";
	}
}
