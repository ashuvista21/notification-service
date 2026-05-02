package com.ashuvista21.notification.channel.variable.resolver;

import java.util.Map ;

import com.ashuvista21.notification.entities.Notification ;
import com.ashuvista21.notification.enums.NotificationChannelType ;

public interface ChannelVariableEnricher {
	NotificationChannelType getChannelType() ;
    Map<String, Object> enrich(Map<String, Object> variables, Notification notification) ;
}
