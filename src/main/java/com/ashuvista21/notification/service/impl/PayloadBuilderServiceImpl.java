package com.ashuvista21.notification.service.impl;

import java.util.Map ;

import org.springframework.stereotype.Service ;
import org.springframework.transaction.annotation.Transactional ;

import com.ashuvista21.notification.dtos.ChannelPayload ;
import com.ashuvista21.notification.entities.Notification ;
import com.ashuvista21.notification.entities.NotificationChannelStatus ;
import com.ashuvista21.notification.entities.UserChannelContact ;
import com.ashuvista21.notification.enums.NotificationType ;
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
	@Transactional
	public ChannelPayload buildPayload(NotificationChannelStatus channelStatus) {
		Notification notification = channelStatus.getNotification() ;
		
		UserChannelContact userChannelContact ;
		if(notification.getNotificationType() == NotificationType.CHANNEL_VERIFICATION)
			userChannelContact = userContactService
				.getUserChannelContact(notification.getUserId(), channelStatus.getChannelType(), false) ;
		else
			userChannelContact = userContactService
			.getUserChannelContact(notification.getUserId(), channelStatus.getChannelType(), true) ;
		
		Map<String, String> variables = variableBuilderService.buildVariables(channelStatus) ;
		
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
