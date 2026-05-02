package com.ashuvista21.notification.channel.impl;

import com.ashuvista21.notification.channel.NotificationChannel;
import com.ashuvista21.notification.dtos.ChannelPayload ;
import com.ashuvista21.notification.enums.NotificationChannelType;

public class EmailNotificationChannel implements NotificationChannel {
	
	@Override
	public NotificationChannelType getChannelType() {
		// TODO Auto-generated method stub
		return NotificationChannelType.EMAIL ;
	}

	@Override
	public void send(ChannelPayload channelPayload) {
		// TODO Auto-generated method stub
		
	}

}
