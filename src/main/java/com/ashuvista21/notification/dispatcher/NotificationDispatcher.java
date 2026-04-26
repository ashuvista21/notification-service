package com.ashuvista21.notification.dispatcher;

import java.util.Map ;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;

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
	
	public void dispatch(Notification notification) {
		/*
		 * 
			1. Receive EventDTO
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

		// 1. Get user preferred channels
		
		
        Set<NotificationChannelType> channels = resolveChannels(notification.getUserId(), notification.getNotificationType()) ;

        // 2. Iterate through channels
        for (NotificationChannelType channel : channels) {

            // create channel status record
        	NotificationChannelStatus channelStatus = NotificationChannelStatus.builder()
                            .notification(notification)
                            .channelType(channel)
                            .status(NotificationStatus.CREATED)
                            .retryCount(0)
                            .build() ;

            // 3. Get service from factory
            NotificationChannel service = factory.getChannel(channel) ;

            try {

                // 4. Send notification
                service.send(notification) ;
                channelStatus.setStatus(NotificationStatus.SENT) ;

            } catch (Exception e) {

                channelStatus.setStatus(NotificationStatus.FAILED) ;
                channelStatus.setErrorMessage(e.getMessage()) ;
                channelStatus.setRetryCount(1) ;

            }

            // attach channel status to notification
            notification.getChannels().add(channelStatus) ;
        }

        // 5. Save notification + channel statuses
        repository.save(notification) ;
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
}
