package com.ashuvista21.notification.channel.variable.resolver.override;

import java.util.HashMap ;
import java.util.Map ;

import org.springframework.stereotype.Component ;

import com.ashuvista21.notification.channel.variable.resolver.NotificationTypeOverride ;
import com.ashuvista21.notification.entities.Notification ;
import com.ashuvista21.notification.enums.NotificationType ;

@Component
public class LoginNewDeviceOverride implements NotificationTypeOverride {
	
	@Override
	public NotificationType getType() {
		return NotificationType.LOGIN_FROM_NEW_DEVICE ;
	}
	
	@Override
	public Map<String, String> override(Notification notification, Map<String, String> baseVariables) {
		Map<String, String> updated = new HashMap<>(baseVariables) ;
		updated.put("ipAddress", notification.getPayload().getOrDefault("ipAddress", "N/A")) ;
		return updated ;
	}

	@Override
	public Map<String, String> defaults() {
		return Map.of(
				"ipAddress", "127.0.0.1"
			) ;
	}
}
