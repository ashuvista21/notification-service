package com.ashuvista21.notification.channel.variable.resolver.channeltype;

import java.util.ArrayList ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;

import com.ashuvista21.notification.channel.variable.resolver.ChannelVariableEnricher ;
import com.ashuvista21.notification.entities.Notification ;
import com.ashuvista21.notification.enums.NotificationChannelType ;

public class WhatsappChannelVariableEnricher implements ChannelVariableEnricher  {
	@Override
    public NotificationChannelType getChannelType() {
        return NotificationChannelType.WHATSAPP ;
    }

	@Override
    public Map<String, Object> enrich(Map<String, Object> variables, Notification notification) {

        Map<String, Object> enriched = new HashMap<>(variables) ;

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
    
    private List<Object> buildParamsList(Map<String, Object> variables) {
        return new ArrayList<>(variables.values()) ;
    }
}
