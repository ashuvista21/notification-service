package com.ashuvista21.notification.channel.variable.resolver.category;

import java.util.Map ;

import org.springframework.stereotype.Component ;

import com.ashuvista21.notification.channel.variable.resolver.BaseVariableResolver ;
import com.ashuvista21.notification.entities.Notification ;
import com.ashuvista21.notification.enums.NotificationCategory ;

@Component
public class InformationCategoryVariableResolver implements BaseVariableResolver{

	@Override
	public NotificationCategory getCategory() {
		return NotificationCategory.INFORMATION ;
	}

	@Override
	public Map<String, String> resolve(Notification notification) {
		
		Map<String, String> metadata = notification.getPayload() ;
		
		return Map.of(
				"name", metadata.getOrDefault("name", "User"),
	            "timestamp", metadata.getOrDefault("timestamp", String.valueOf(System.currentTimeMillis())),
	            "type", notification.getNotificationType().name()
	        ) ;
	}
	
	@Override
	public Map<String, String> defaults() {
		
		return Map.of(
				"name", "User",
	            "message", "Information update",
	            "timestamp", "Date and Time of the information",
	            "type", "Notification Type"
	        ) ;
	}

}
