package com.ashuvista21.notification.dtos;

import java.util.Map ;

public record NotificationInboundEvent(
		String eventId,
		String userId,
		String eventType,
		String eventContext,
		Map<String, String> payload) {

}
