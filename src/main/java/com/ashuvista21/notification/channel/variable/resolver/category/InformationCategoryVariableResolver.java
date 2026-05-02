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
	public Map<String, Object> resolve(Notification notification) {
		
		Map<String, Object> metadata = notification.getPayload() ;
		
		return Map.of(
				"name", metadata.getOrDefault("name", "User"),
	            "message", metadata.getOrDefault("message", "Information update"),
	            "timestamp", metadata.getOrDefault("timestamp", System.currentTimeMillis()),
	            "type", notification.getNotificationType().name()
	        ) ;
	}

}
