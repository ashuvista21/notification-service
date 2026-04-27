package com.ashuvista21.notification.dispatcher;

import java.util.Map ;
import java.util.Set;
import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener ;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional ;

import com.ashuvista21.notification.channel.NotificationChannel;
import com.ashuvista21.notification.entities.Notification;
import com.ashuvista21.notification.entities.NotificationChannelStatus;
import com.ashuvista21.notification.enums.NotificationCategory ;
import com.ashuvista21.notification.enums.NotificationChannelType;
import com.ashuvista21.notification.enums.NotificationStatus;
import com.ashuvista21.notification.enums.NotificationType ;
import com.ashuvista21.notification.factory.NotificationChannelFactory;
import com.ashuvista21.notification.repository.NotificationRepository;
import com.ashuvista21.notification.service.UserNotificationCategoryPreferenceService ;
import com.ashuvista21.notification.service.UserNotificationTypePreferenceService ;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class NotificationDispatcher {
	
	private final UserNotificationCategoryPreferenceService notificationCategoryPreferenceService ;
	private final UserNotificationTypePreferenceService notificationTypePreferenceService ;
	private final NotificationChannelFactory factory ;
	private final NotificationRepository repository ;
	
	private static final Map<NotificationCategory, Set<NotificationChannelType>> DEFAULT_CHANNELS =
	        Map.of(
	            NotificationCategory.OTP, Set.of(NotificationChannelType.SMS),
	            NotificationCategory.SECURITY, Set.of(NotificationChannelType.EMAIL, NotificationChannelType.SMS),
	            NotificationCategory.TRANSACTIONAL, Set.of(NotificationChannelType.EMAIL),
	            NotificationCategory.INFORMATION, Set.of(NotificationChannelType.EMAIL),
	            NotificationCategory.PROMOTIONAL, Set.of(NotificationChannelType.PUSH)
	        );
	
	@Transactional
	public void dispatch(Notification notification) {
		/*
		 * 
			1. Receive EventDTO / POST Call
			2. Resolve channels (based on preferences + defaults)
			3. Create Notification object
				status = PENDING
				create NotificationChannelStatus entries (one per channel)
			4. Persist notification in DB
			5. Publish to dispatcher (or async process)
			6.For each channel:
				Call provider via factory
				Update channel status (SENT, FAILED)
				Store providerMessageId
			7. Update overall notification status
		 * 
		 */

		// Resolve channels (based on preferences + defaults)
        Set<NotificationChannelType> channels = resolveChannels(notification.getUserId(), notification.getNotificationType()) ;

        // Iterate through channels
        for (NotificationChannelType channel : channels) {

            // create channel status record
        	NotificationChannelStatus channelStatus = NotificationChannelStatus.builder()
                            .notification(notification)
                            .channelType(channel)
                            .status(NotificationStatus.CREATED)
                            .retryCount(0)
                            .build() ;
        	
        	notification.getChannels().add(channelStatus) ;
        }
        
        // Persist notification in DB
        Notification entityNotification = repository.save(notification) ;
        
        publish(notification.getId());
        
        for (NotificationChannelStatus channelStatus : entityNotification.getChannels()) {
            
        	// Publish to dispatcher (or async process)
        	NotificationChannel service = factory.getChannel(channelStatus.getChannelType()) ;
        	
            try {
                // Send notification
                service.send(notification) ;
                channelStatus.setStatus(NotificationStatus.SENT) ;
            } catch (Exception e) {
                channelStatus.setStatus(NotificationStatus.FAILED) ;
                channelStatus.setErrorMessage(e.getMessage()) ;
                channelStatus.setRetryCount(1) ;
            }
        }        
	}
	
	@Transactional
	@KafkaListener(topics = "notification-dispatch")
	public void process(UUID notificationId) {

	    Notification notification = repository.findById(notificationId)
	            .orElseThrow(() -> new RuntimeException("Invalid Notification identifier : " + notificationId.toString())) ;

	    for (NotificationChannelStatus channelStatus : notification.getChannels()) {

	        NotificationChannel service =
	                factory.getChannel(channelStatus.getChannelType()) ;

	        try {
	            //String providerId = service.send(notification) ;
	            service.send(notification) ;

	            channelStatus.setStatus(NotificationStatus.SENT) ;
	            //channelStatus.setProviderMessageId(providerId) ;

	        } catch (Exception e) {
	            channelStatus.setStatus(NotificationStatus.FAILED) ;
	            channelStatus.setErrorMessage(e.getMessage()) ;
	            channelStatus.setRetryCount(channelStatus.getRetryCount() + 1) ;
	        }
	    }

	    notification.setStatus(calculateOverallStatus(notification));
	}
	
	private Set<NotificationChannelType> resolveChannels(UUID userId, NotificationType notificationType) {

	    // 1. Check type override
		Set<NotificationChannelType> overrideChannels = notificationTypePreferenceService.getUserChannels(userId, notificationType) ;
		
		if(!overrideChannels.isEmpty()) {
			return overrideChannels ;
		}
		
		// 2. Fallback to category
	    NotificationCategory category = notificationType.getCategory() ;

	    Set<NotificationChannelType> categoryChannels = notificationCategoryPreferenceService.getUserChannels(userId, category) ;
		
	    if(!categoryChannels.isEmpty()) {
			return categoryChannels ;
		}
	    
	    // 3. System default (THIS is defaultChannels)
	    return DEFAULT_CHANNELS.getOrDefault(category, Set.of(NotificationChannelType.EMAIL)) ;
	}
	
	private NotificationStatus calculateOverallStatus(Notification notification) {
		int size = notification.getChannels().size() ;
		int success = 0 ;
		boolean errorFlag = false ;
		boolean failedFlag = false ;
		
		for (NotificationChannelStatus channelStatus : notification.getChannels()) {
			if(channelStatus.getStatus().equals(NotificationStatus.SENT)) {
				++success ;
				continue ;
			}
			
			if(!errorFlag && channelStatus.getStatus().equals(NotificationStatus.ERROR)) {
				errorFlag = true ;
			}
			
			if(!failedFlag && channelStatus.getStatus().equals(NotificationStatus.FAILED)) {
				failedFlag = true ;
			}
		}
		
		if(errorFlag)
			return NotificationStatus.ERROR ;
		else if(failedFlag)
			return NotificationStatus.FAILED ;
		else if(success == size)
			return NotificationStatus.SENT ;
		else
			return NotificationStatus.PENDING ;
	}
	
	private void publish(UUID notificationId) {
		//produce kafka eevnt
	}
}
