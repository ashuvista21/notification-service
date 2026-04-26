package com.ashuvista21.notification.channel.impl;

import org.springframework.stereotype.Service;

import com.ashuvista21.notification.channel.NotificationChannel;
import com.ashuvista21.notification.entities.Notification;
import com.ashuvista21.notification.enums.NotificationChannelType;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SMSNotificationChannel implements NotificationChannel {
	
	@PostConstruct
	private void setSmsGatewayDeviceToken() {
		
	}
	
	@Override
	public NotificationChannelType getChannelType() {
		return NotificationChannelType.SMS ;
	}

	@Override
	public void send(Notification notification) {
		
	}

}
