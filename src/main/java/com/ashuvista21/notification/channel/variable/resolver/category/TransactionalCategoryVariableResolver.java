package com.ashuvista21.notification.channel.variable.resolver.category;

import java.util.Map ;

import org.springframework.stereotype.Component ;

import com.ashuvista21.notification.channel.variable.resolver.BaseVariableResolver ;
import com.ashuvista21.notification.entities.Notification ;
import com.ashuvista21.notification.enums.NotificationCategory ;

@Component
public class TransactionalCategoryVariableResolver implements BaseVariableResolver{

	@Override
	public NotificationCategory getCategory() {
		return NotificationCategory.TRANSACTIONAL ;
	}

	@Override
	public Map<String, Object> resolve(Notification notification) {
		
		Map<String, Object> metadata = notification.getPayload() ;
		
		return Map.of(
				"amount", metadata.get("amount"),
	            "txnId", metadata.get("txnId"),
	            "status", notification.getNotificationType().name()
	        ) ;
	}

}
