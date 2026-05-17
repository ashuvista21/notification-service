package com.ashuvista21.notification.channel.variable.resolver.channeltype;

import java.util.HashMap ;
import java.util.Map ;

import com.ashuvista21.notification.channel.variable.resolver.ChannelVariableEnricher ;
import com.ashuvista21.notification.entities.Notification ;
import com.ashuvista21.notification.enums.NotificationChannelType ;

public class SmsChannelVariableEnricher implements ChannelVariableEnricher  {
	@Override
    public NotificationChannelType getChannelType() {
        return NotificationChannelType.SMS ;
    }

    @Override
    public Map<String, Object> enrich(Map<String, Object> variables, Notification notification) {

        Map<String, Object> enriched = new HashMap<>(variables) ;

        // Keep only essential fields for SMS
        enriched.put("message", buildSmsMessage(variables)) ;

        return enriched ;
    }

    private String buildSmsMessage(Map<String, Object> variables) {
        if (variables.containsKey("otp")) {
            return "Your OTP is " + variables.get("otp") ;
        }
        return (String) variables.getOrDefault("message", "Notification") ;
    }
}
