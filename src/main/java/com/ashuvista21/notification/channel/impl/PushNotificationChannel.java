package com.ashuvista21.notification.channel.impl;

import com.ashuvista21.notification.channel.NotificationChannel;
import com.ashuvista21.notification.dtos.ChannelPayload ;
import com.ashuvista21.notification.enums.NotificationChannelType;

public class PushNotificationChannel implements NotificationChannel {

	@Override
	public NotificationChannelType getChannelType() {
		return NotificationChannelType.PUSH ;
	}

	@Override
	public void send(ChannelPayload channelPayload) {
		// TODO Auto-generated method stub

	}

}
