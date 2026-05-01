package com.ashuvista21.notification.channel.impl;

import org.springframework.stereotype.Service;

import com.ashuvista21.notification.channel.NotificationChannel;
import com.ashuvista21.notification.entities.NotificationChannelStatus ;
import com.ashuvista21.notification.enums.NotificationChannelType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SMSNotificationChannel implements NotificationChannel {
	
	@Override
	public NotificationChannelType getChannelType() {
		return NotificationChannelType.SMS ;
	}

	@Override
	public void send(NotificationChannelStatus channelStatus) {
		
	}

}
