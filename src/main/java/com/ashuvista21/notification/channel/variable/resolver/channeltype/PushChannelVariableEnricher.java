package com.ashuvista21.notification.channel.variable.resolver.channeltype;

import java.util.HashMap ;
import java.util.Map ;

import com.ashuvista21.notification.channel.variable.resolver.ChannelVariableEnricher ;
import com.ashuvista21.notification.entities.Notification ;
import com.ashuvista21.notification.enums.NotificationChannelType ;

public class PushChannelVariableEnricher implements ChannelVariableEnricher  {
	@Override
    public NotificationChannelType getChannelType() {
        return NotificationChannelType.PUSH ;
    }

	@Override
    public Map<String, Object> enrich(Map<String, Object> variables, Notification notification) {

        Map<String, Object> enriched = new HashMap<>(variables) ;

        enriched.put("title", buildTitle(notification)) ;
        enriched.put("body", buildBody(variables)) ;
        enriched.put("deepLink", resolveDeepLink(notification)) ;

        return enriched ;
    }

    private String buildTitle(Notification notification) {
        return switch (notification.getNotificationType()) {
            case PAYMENT_SUCCESS -> "Payment Success ✅" ;
            case PAYMENT_FAILED -> "Payment Failed ❌" ;
            case LOGIN_FROM_NEW_DEVICE -> "New Login Detected" ;
            default -> "Notification" ;
        } ;
    }

    private String buildBody(Map<String, Object> variables) {
        if (variables.containsKey("otp")) {
            return "Your OTP is " + variables.get("otp") ;
        }
        return (String) variables.getOrDefault("message", "You have a new update") ;
    }

    private String resolveDeepLink(Notification notification) {
        return switch (notification.getNotificationType()) {
            case PAYMENT_SUCCESS -> "app://payments" ;
            case ORDER_PLACED -> "app://orders" ;
            default -> "app://home" ;
        } ;
    }
}
