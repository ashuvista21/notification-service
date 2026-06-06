package com.ashuvista21.notification.channel.variable.resolver.category;

import java.util.Map ;

import org.springframework.stereotype.Component ;

import com.ashuvista21.notification.channel.variable.resolver.BaseVariableResolver ;
import com.ashuvista21.notification.entities.Notification ;
import com.ashuvista21.notification.enums.NotificationCategory ;

@Component
public class OtpCategoryVariableResolver implements BaseVariableResolver{

	@Override
	public NotificationCategory getCategory() {
		return NotificationCategory.OTP ;
	}

	@Override
	public Map<String, String> resolve(Notification notification) {
		
		Map<String, String> metadata = notification.getPayload() ;
		
		return Map.of(
	            "otp", metadata.get("otp"),
	            "expiry", metadata.get("expiry")
	        ) ;
	}
	
	@Override
	public Map<String, String> defaults() {
		
		return Map.of(
				"otp", "One Time Password",
	            "expiry", "Date and Time of OTP Expiry"
	        ) ;
	}

}
