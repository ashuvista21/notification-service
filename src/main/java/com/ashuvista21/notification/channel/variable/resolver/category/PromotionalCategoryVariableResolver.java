package com.ashuvista21.notification.channel.variable.resolver.category;

import java.util.Map ;

import org.springframework.stereotype.Component ;

import com.ashuvista21.notification.channel.variable.resolver.BaseVariableResolver ;
import com.ashuvista21.notification.entities.Notification ;
import com.ashuvista21.notification.enums.NotificationCategory ;

@Component
public class PromotionalCategoryVariableResolver implements BaseVariableResolver{

	@Override
	public NotificationCategory getCategory() {
		return NotificationCategory.PROMOTIONAL ;
	}

	@Override
	public Map<String, Object> resolve(Notification notification) {
		
		Map<String, Object> metadata = notification.getPayload() ;
		
		return Map.of(
				"name", metadata.getOrDefault("name", "User"),
	            "offerTitle", metadata.getOrDefault("offerTitle", "Special Offer"),
	            "discount", metadata.getOrDefault("discount", "0%"),
	            "expiryDate", metadata.getOrDefault("expiryDate", "N/A"),
	            "cta", metadata.getOrDefault("cta", "Shop Now")
	        ) ;
	}

}
