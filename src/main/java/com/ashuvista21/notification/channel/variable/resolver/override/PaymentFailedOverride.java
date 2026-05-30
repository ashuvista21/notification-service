package com.ashuvista21.notification.channel.variable.resolver.override;

import java.util.HashMap ;
import java.util.Map ;

import com.ashuvista21.notification.channel.variable.resolver.NotificationTypeOverride ;
import com.ashuvista21.notification.entities.Notification ;
import com.ashuvista21.notification.enums.NotificationType ;

public class PaymentFailedOverride implements NotificationTypeOverride {
	
	@Override
	public NotificationType getType() {
		return NotificationType.PAYMENT_FAILED ;
	}
	
	@Override
	public Map<String, String> override(Notification notification, Map<String, String> baseVariables) {
		Map<String, String> updated = new HashMap<>(baseVariables) ;
		updated.put("reason", notification.getPayload().getOrDefault("reason", "Unknown")) ;
		return updated ;
	}

	@Override
	public Map<String, String> defaults() {
		return Map.of(
				"reason", "Reason for payment failure"
			) ;
	}
}
