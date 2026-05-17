package com.ashuvista21.notification.service.impl;

import java.util.Map ;

import org.springframework.stereotype.Service ;

import com.ashuvista21.notification.dtos.ChannelPayload ;
import com.ashuvista21.notification.entities.Notification ;
import com.ashuvista21.notification.entities.NotificationChannelStatus ;
import com.ashuvista21.notification.entities.UserChannelContact ;
import com.ashuvista21.notification.service.PayloadBuilderService ;
import com.ashuvista21.notification.service.UserChannelContactService ;
import com.ashuvista21.notification.service.VariableBuilderService ;

import lombok.RequiredArgsConstructor ;

@Service
@RequiredArgsConstructor
public class PayloadBuilderServiceImpl implements PayloadBuilderService{
	
	private final UserChannelContactService userContactService ;
	private final VariableBuilderService variableBuilderService ;
	
	@Override
	public ChannelPayload buildPayload(NotificationChannelStatus channelStatus) {
		Notification notification = channelStatus.getNotification() ;
		
		UserChannelContact userChannelContact = userContactService
				.getVerifiedUserChannelContact(notification.getUserId(), channelStatus.getChannelType()) ;
		
		Map<String, Object> variables = variableBuilderService.buildVariables(channelStatus) ;
		
		ChannelPayload payload = ChannelPayload.builder()
				.notificationId(notification.getId().toString())
				.channelId(channelStatus.getId().toString())
				.userId(notification.getUserId().toString())
				.notificationType(notification.getNotificationType().toString())
				.channelType(channelStatus.getChannelType().toString())
				.notificationCategory(notification.getCategory().toString())
				.recipientAddress(userChannelContact.getValue())
				.variables(variables)
				.build() ;
		
		return payload ;
	}
	
}
