package com.ashuvista21.notification.channel.variable.resolver.channeltype;

import java.util.HashMap ;
import java.util.Map ;

import org.springframework.stereotype.Component ;

import com.ashuvista21.notification.channel.variable.resolver.ChannelVariableEnricher ;
import com.ashuvista21.notification.entities.Notification ;
import com.ashuvista21.notification.enums.NotificationChannelType ;

@Component
public class WhatsappChannelVariableEnricher implements ChannelVariableEnricher {
	@Override
    public NotificationChannelType getChannelType() {
        return NotificationChannelType.WHATSAPP ;
    }

	@Override
    public Map<String, String> enrich(Map<String, String> variables, Notification notification) {

        Map<String, String> enriched = new HashMap<>(variables) ;

        enriched.put("templateName", resolveTemplate(notification)) ;
        enriched.put("language", "en") ;

        // WhatsApp expects ordered params sometimes
        enriched.put("params", buildParamsList(variables)) ;

        return enriched ;
    }

    private String resolveTemplate(Notification notification) {
        return switch (notification.getNotificationType()) {
            case TRANSACTION_OTP -> "otp_template" ;
            case PAYMENT_SUCCESS -> "payment_success_template" ;
            default -> "generic_template" ;
        };
    }
    
    private String buildParamsList(Map<String, String> variables) {
        return variables.values().toString() ;
    }

    @Override
	public Map<String, String> defaults() {

        return Map.of(
        		"templateName", "generic_template",
        		"language", "en",
        		"params", "values of all keys"
        	) ;
	}
}
