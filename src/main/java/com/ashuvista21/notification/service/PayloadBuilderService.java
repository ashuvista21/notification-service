package com.ashuvista21.notification.service;

import com.ashuvista21.notification.dtos.ChannelPayload ;
import com.ashuvista21.notification.entities.Notification ;
import com.ashuvista21.notification.entities.NotificationChannelStatus ;

public interface PayloadBuilderService {
	ChannelPayload buildPayload(NotificationChannelStatus channelStatus, Notification notification) ;
}
