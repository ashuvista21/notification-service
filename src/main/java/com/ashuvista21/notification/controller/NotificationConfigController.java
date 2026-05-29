package com.ashuvista21.notification.controller;

import org.springframework.web.bind.annotation.GetMapping ;
import org.springframework.web.bind.annotation.RequestMapping ;
import org.springframework.web.bind.annotation.RestController ;

import lombok.RequiredArgsConstructor ;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification-configs")
public class NotificationConfigController {

	@GetMapping("{eventType}/variables")
	public String getConfigVariablesForEventType() {
		return null ;
	}
}
