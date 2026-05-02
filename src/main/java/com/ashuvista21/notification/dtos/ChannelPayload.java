package com.ashuvista21.notification.dtos;

import java.util.Map ;

import lombok.Builder ;
import lombok.Getter ;
import lombok.Setter ;

@Builder
@Getter
@Setter
public class ChannelPayload {
		String notificationId ;
		String channelId ;
		String userId ;
		String notificationType ;
		String channelType ;
		String recipientAddress ;
		Map<String, Object> variables ;
}
