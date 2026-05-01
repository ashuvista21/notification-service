package com.ashuvista21.notification.channel;

import com.ashuvista21.notification.entities.NotificationChannelStatus ;
import com.ashuvista21.notification.enums.NotificationChannelType;

public interface NotificationChannel {
	NotificationChannelType getChannelType() ;
    void send(NotificationChannelStatus notificationChannelStatus) ;
}
