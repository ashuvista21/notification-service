package com.ashuvista21.notification.channel.variable.resolver;

import java.util.Map ;

import com.ashuvista21.notification.entities.Notification ;
import com.ashuvista21.notification.enums.NotificationType ;

public interface NotificationTypeOverride {
	
	NotificationType getType() ;
    Map<String, String> override(Notification notification, Map<String, String> baseVariables) ;
    Map<String, String> defaults() ;
}
