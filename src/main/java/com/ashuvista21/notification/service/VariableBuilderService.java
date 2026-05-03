package com.ashuvista21.notification.service;

import java.util.Map ;

import com.ashuvista21.notification.entities.NotificationChannelStatus ;

public interface VariableBuilderService {
	Map<String, Object> buildVariables(NotificationChannelStatus channelStatus) ;
}
