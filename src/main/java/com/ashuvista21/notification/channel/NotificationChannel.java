package com.ashuvista21.notification.channel;

import com.ashuvista21.notification.dtos.ChannelPayload ;
import com.ashuvista21.notification.enums.NotificationChannelType;

public interface NotificationChannel {
	NotificationChannelType getChannelType() ;
    void send(ChannelPayload channelPayload) ;
}
