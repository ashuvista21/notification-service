package com.ashuvista21.notification.service;

import java.util.Map ;

import com.ashuvista21.notification.dtos.NotificationDefaultValuesView ;
import com.ashuvista21.notification.entities.NotificationChannelStatus ;
import com.ashuvista21.notification.enums.NotificationChannelType ;
import com.ashuvista21.notification.enums.NotificationType ;

public interface VariableBuilderService {
	Map<String, Object> buildVariables(NotificationChannelStatus channelStatus) ;
	NotificationDefaultValuesView getDefaultValues(NotificationType type, NotificationChannelType channel) ;
}
