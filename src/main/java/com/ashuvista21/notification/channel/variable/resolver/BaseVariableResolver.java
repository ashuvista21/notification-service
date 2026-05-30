package com.ashuvista21.notification.channel.variable.resolver;

import java.util.Map ;

import com.ashuvista21.notification.entities.Notification ;
import com.ashuvista21.notification.enums.NotificationCategory ;

public interface BaseVariableResolver {
	NotificationCategory getCategory() ;
    Map<String, Object> resolve(Notification notification) ;
    Map<String, String> defaults() ;
}