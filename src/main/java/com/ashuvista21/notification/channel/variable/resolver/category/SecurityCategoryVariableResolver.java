package com.ashuvista21.notification.channel.variable.resolver.category;

import java.util.Map ;

import org.springframework.stereotype.Component ;

import com.ashuvista21.notification.channel.variable.resolver.BaseVariableResolver ;
import com.ashuvista21.notification.entities.Notification ;
import com.ashuvista21.notification.enums.NotificationCategory ;

@Component
public class SecurityCategoryVariableResolver implements BaseVariableResolver{

	@Override
	public NotificationCategory getCategory() {
		return NotificationCategory.SECURITY ;
	}

	@Override
	public Map<String, String> resolve(Notification notification) {
		
		Map<String, String> metadata = notification.getPayload() ;
		
		return Map.of(
				"name", metadata.getOrDefault("name", "User"),
	            "event", notification.getNotificationType().name(),
	            "device", metadata.getOrDefault("device", "Unknown Device"),
	            "location", metadata.getOrDefault("location", "Unknown Location"),
	            "time", metadata.getOrDefault("time", String.valueOf(System.currentTimeMillis()))
	        ) ;
	}
	
	@Override
	public Map<String, String> defaults() {
		
		return Map.of(
				"name", "User",
	            "event", "Event Name",
	            "device", "Unknown Device",
	            "location", "Unknown Location",
	            "time", "Time of the event"
	        ) ;
	}

}
