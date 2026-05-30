package com.ashuvista21.notification.channel.variable.resolver.channeltype;

import java.time.Year ;
import java.util.HashMap ;
import java.util.Map ;

import com.ashuvista21.notification.channel.variable.resolver.ChannelVariableEnricher ;
import com.ashuvista21.notification.entities.Notification ;
import com.ashuvista21.notification.enums.NotificationChannelType ;

public class EmailChannelVariableEnricher implements ChannelVariableEnricher  {
	@Override
    public NotificationChannelType getChannelType() {
        return NotificationChannelType.EMAIL ;
    }

	@Override
    public Map<String, Object> enrich(Map<String, Object> variables, Notification notification) {

        Map<String, Object> enriched = new HashMap<>(variables) ;

        enriched.put("subject", buildSubject(notification)) ;
        enriched.put("supportEmail", "support@yourapp.com") ;
        enriched.put("companyName", "YourApp") ;
        enriched.put("year", Year.now().getValue()) ;
        enriched.put("templateName", "templateName") ;

        return enriched;
    }

    private String buildSubject(Notification notification) {
        return switch (notification.getNotificationType()) {
            case PAYMENT_SUCCESS -> "Payment Successful" ;
            case PAYMENT_FAILED -> "Payment Failed" ;
            case PASSWORD_CHANGED -> "Security Alert" ;
            default -> "New Notification" ;
        } ;
    }

	@Override
	public Map<String, String> defaults() {

        return Map.of(
        		"subject", "Email Subject",
        		"supportEmail", "support@yourapp.com",
        		"companyName", "YourApp",
        		"templateName", "Template",
        		"year", Year.now().toString()
        	) ;
	}
}
