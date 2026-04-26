package com.ashuvista21.notification.channel;

import com.ashuvista21.notification.entities.Notification;
import com.ashuvista21.notification.enums.NotificationChannelType;

public interface NotificationChannel {
	NotificationChannelType getChannelType() ;
    void send(Notification notification) ;
}
