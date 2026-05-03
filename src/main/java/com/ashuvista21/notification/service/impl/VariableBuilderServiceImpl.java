package com.ashuvista21.notification.service.impl;

import java.util.Map ;

import org.springframework.stereotype.Service ;

import com.ashuvista21.notification.entities.Notification ;
import com.ashuvista21.notification.entities.NotificationChannelStatus ;
import com.ashuvista21.notification.factory.BaseVariableResolverFactory ;
import com.ashuvista21.notification.factory.ChannelEnricherFactory ;
import com.ashuvista21.notification.factory.NotificationTypeOverrideFactory ;
import com.ashuvista21.notification.service.VariableBuilderService ;

import lombok.RequiredArgsConstructor ;

@Service
@RequiredArgsConstructor
public class VariableBuilderServiceImpl implements VariableBuilderService{
	
	private final BaseVariableResolverFactory baseVariableResolverFactory ;
	private final ChannelEnricherFactory channelEnricherFactory ;
	private final NotificationTypeOverrideFactory notificationTypeOverrideFactory ;
	
	@Override
	public Map<String, Object> buildVariables(NotificationChannelStatus channelStatus) {
		Notification notification = channelStatus.getNotification() ;

	    if (notification == null) {
	        throw new IllegalStateException("ChannelStatus must have a Notification") ;
	    }
	    /*
	     * 1. Category → base data (business)
	     * 2. NotificationType override → fix/specialize business data
	     * 3. Channel enricher → format for delivery
	     */

		// 1️⃣ Base (Category-based)
	    Map<String, Object> baseVariables = baseVariableResolverFactory
				.get(notification.getNotificationType())
				.resolve(notification) ;
		
		// 2️⃣ Apply override (if exists)
	    Map<String, Object> overriddenVariables = notificationTypeOverrideFactory
	            .get(notification.getNotificationType())
	            .map(override -> override.override(notification, baseVariables))
	            .orElse(baseVariables) ;

	    // 3️⃣ Channel enrichment
	    Map<String, Object> finalVariables = channelEnricherFactory
	            .get(channelStatus.getChannelType())
	            .enrich(overriddenVariables, notification) ;
	    // Variables used inside a lambda must not be reassigned
	    //log.debug("Variables built for type={} channel={} -> {}", type, channelType, finalVariables);

	    return finalVariables ;
	}

}
