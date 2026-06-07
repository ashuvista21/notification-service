package com.ashuvista21.notification.service.impl;

import java.util.Map ;

import org.springframework.stereotype.Service ;

import com.ashuvista21.notification.dtos.NotificationDefaultValuesView ;
import com.ashuvista21.notification.entities.Notification ;
import com.ashuvista21.notification.entities.NotificationChannelStatus ;
import com.ashuvista21.notification.enums.NotificationChannelType ;
import com.ashuvista21.notification.enums.NotificationType ;
import com.ashuvista21.notification.exceptions.notification.NotificationChannelMismatchedException ;
import com.ashuvista21.notification.exceptions.notification.NotificationChannelNotFoundException ;
import com.ashuvista21.notification.exceptions.notification.NotificationNotFoundException ;
import com.ashuvista21.notification.factory.BaseVariableResolverFactory ;
import com.ashuvista21.notification.factory.ChannelEnricherFactory ;
import com.ashuvista21.notification.factory.NotificationTypeOverrideFactory ;
import com.ashuvista21.notification.repository.NotificationChannelStatusRepository ;
import com.ashuvista21.notification.service.VariableBuilderService ;

import lombok.RequiredArgsConstructor ;

@Service
@RequiredArgsConstructor
public class VariableBuilderServiceImpl implements VariableBuilderService{
	
	private final BaseVariableResolverFactory baseVariableResolverFactory ;
	private final ChannelEnricherFactory channelEnricherFactory ;
	private final NotificationTypeOverrideFactory notificationTypeOverrideFactory ;
	private final NotificationChannelStatusRepository channelStatusRepository ;
	
	@Override
	public Map<String, String> buildVariables(NotificationChannelStatus channelStatus, Notification notification) {

	    if (notification == null) {
	        throw new NotificationNotFoundException("Notification cannot be null") ;
	    }
	    
	    if (channelStatus == null) {
	        throw new NotificationChannelNotFoundException("Channel status cannot be null") ;
	    }
	    
	    if(!channelStatusRepository.existsByIdAndNotificationId(
	    		channelStatus.getId(),
	    		notification.getId())) {
	        throw new NotificationChannelMismatchedException("Channel status does not belong to the given notification") ;
	    }
	    
	    
	    /*
	     * 1. Category → base data (business)
	     * 2. NotificationType override → fix/specialize business data
	     * 3. Channel enricher → format for delivery
	     */

		// 1️⃣ Base (Category-based)
	    Map<String, String> baseVariables = baseVariableResolverFactory
				.get(notification.getNotificationType())
				.resolve(notification) ;
		
		// 2️⃣ Apply override (if exists)
	    Map<String, String> overriddenVariables = notificationTypeOverrideFactory
	            .get(notification.getNotificationType())
	            .map(override -> override.override(notification, baseVariables))
	            .orElse(baseVariables) ;

	    // 3️⃣ Channel enrichment
	    Map<String, String> finalVariables = channelEnricherFactory
	            .get(channelStatus.getChannelType())
	            .enrich(overriddenVariables, notification) ;
	    // Variables used inside a lambda must not be reassigned
	    //log.debug("Variables built for type={} channel={} -> {}", type, channelType, finalVariables);

	    return finalVariables ;
	}
	
	@Override
	public NotificationDefaultValuesView getDefaultValues(
			NotificationType type,
			NotificationChannelType channel) {
		
		Map<String, String> baseDefaults = baseVariableResolverFactory
				.get(type)
				.defaults() ;
		
		Map<String, String> override = notificationTypeOverrideFactory
				.get(type)
				.map(extra -> extra.defaults())
				.orElse(null) ;
		
		Map<String, String> enricher = channelEnricherFactory
				.get(channel)
				.defaults() ;
		
		return new NotificationDefaultValuesView(
				baseDefaults,
				override,
				enricher) ;
	}

}
