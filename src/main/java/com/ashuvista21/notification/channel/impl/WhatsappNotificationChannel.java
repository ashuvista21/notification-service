package com.ashuvista21.notification.channel.impl;

import com.ashuvista21.notification.channel.NotificationChannel;
import com.ashuvista21.notification.entities.Notification;
import com.ashuvista21.notification.enums.NotificationChannelType;

public class WhatsappNotificationChannel implements NotificationChannel {

	@Override
	public NotificationChannelType getChannelType() {
		return NotificationChannelType.WHATSAPP ;
	}

	@Override
	public void send(Notification notification) {
		// TODO Auto-generated method stub

	}

}
