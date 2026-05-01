package com.ashuvista21.notification.dispatcher;

import java.util.Map ;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService ;
import java.util.concurrent.Executors ;

import org.springframework.kafka.annotation.KafkaListener ;
import org.springframework.kafka.core.KafkaTemplate ;
import org.springframework.messaging.handler.annotation.Payload ;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional ;

import com.ashuvista21.notification.channel.NotificationChannel;
import com.ashuvista21.notification.dtos.NotificationDispatcherDTO ;
import com.ashuvista21.notification.entities.Notification;
import com.ashuvista21.notification.entities.NotificationChannelStatus;
import com.ashuvista21.notification.enums.NotificationCategory ;
import com.ashuvista21.notification.enums.NotificationChannelType;
import com.ashuvista21.notification.enums.NotificationStatus;
import com.ashuvista21.notification.enums.NotificationType ;
import com.ashuvista21.notification.factory.NotificationChannelFactory;
import com.ashuvista21.notification.repository.NotificationChannelStatusRepository ;
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
	
	private final NotificationRepository notificationRepository ;
	private final NotificationChannelStatusRepository channelStatusRepository ;
	
	private final ExecutorService executorService = Executors.newFixedThreadPool(5) ;
	
	private final KafkaTemplate<String, NotificationDispatcherDTO> kafkaTemplate;
    private final String topic = "notification-dispatch" ;
	
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
        notificationRepository.save(notification) ;
        
        publish(new NotificationDispatcherDTO(notification.getId().toString())) ;       
	}
	
	@KafkaListener(topics = topic)
	public void process(@Payload NotificationDispatcherDTO notificationDispatcherDTO) {
		
		UUID notificationId = UUID.fromString(notificationDispatcherDTO.notificationId()) ;

		Notification notification = notificationRepository.findById(notificationId)
	            .orElseThrow(() -> new RuntimeException(
	                    "Invalid Notification identifier : " + notificationId));

	    for (NotificationChannelStatus channelStatus : notification.getChannels()) {

	        executorService.submit(() ->
	                processChannel(channelStatus.getId())
	        );
	    }
	}
	
	private void processChannel(UUID notificationChannelStatusId) {
	    
	    NotificationChannelStatus channelStatus = getChannelStatus(notificationChannelStatusId) ;
	    
	    Notification notification = channelStatus.getNotification() ;

	    NotificationChannel service = factory.getChannel(channelStatus.getChannelType()) ;

	    try {
	        service.send(channelStatus) ;
	        markSuccess(notificationChannelStatusId) ;
	    } catch (Exception e) {
	        markFailed(notificationChannelStatusId, e) ;
	    }
	    
	    notification.setStatus(calculateOverallStatus(notification)) ;
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
		int total = notification.getChannels().size() ;

	    if (total == 0) {
	        return NotificationStatus.PENDING ; // or FAILED based on your design
	    }

	    int success = 0 ;
	    boolean hasError = false ;
	    boolean hasFailure = false ;

	    for (NotificationChannelStatus channelStatus : notification.getChannels()) {
	        NotificationStatus status = channelStatus.getStatus() ;

	        if (status == NotificationStatus.SENT) {
	            success++ ;
	        } else if (status == NotificationStatus.FAILED) {
	            hasFailure = true ;
	        } else if (status == NotificationStatus.ERROR) {
	            hasError = true ;
	        }
	    }

	    // ✅ All success
	    if (success == total) {
	        return NotificationStatus.SENT ;
	    }

	    // ✅ Partial success (most important addition)
	    if (success > 0) {
	        return NotificationStatus.PARTIALLY_SENT ;
	    }

	    // ✅ No success cases
	    if (hasFailure) {
	        return NotificationStatus.FAILED ;
	    }

	    if (hasError) {
	        return NotificationStatus.ERROR ;
	    }

	    return NotificationStatus.PENDING ;
	}
	
	private void publish(NotificationDispatcherDTO dispatcherDTO) {
		kafkaTemplate.send(topic, dispatcherDTO.notificationId().toString(), dispatcherDTO)
        .whenComplete((result, ex) -> {
            if (ex != null) {
                // Failure handling
                //log.error("Failed to publish notification: {}", dispatcherDTO.getNotificationId(), ex);
            } else {
                // Success handling
                //log.info("Published notification: {} to partition: {}",
                //        dispatcherDTO.getNotificationId(),
                //        result.getRecordMetadata().partition());
            }
        });
	}
	
	@Transactional
	private void markSuccess(UUID channelStatusId) {
		NotificationChannelStatus cs = channelStatusRepository.findById(channelStatusId).orElseThrow() ;
	    cs.setStatus(NotificationStatus.SENT) ;
	}
	
	@Transactional
	private void markFailed(UUID channelStatusId, Exception e) {
		NotificationChannelStatus cs = channelStatusRepository.findById(channelStatusId).orElseThrow() ;
	    cs.setStatus(NotificationStatus.FAILED) ;
	    cs.setErrorMessage(e.getMessage()) ;
	    cs.setRetryCount(cs.getRetryCount() + 1) ;
	}
	
	@Transactional
	public NotificationChannelStatus getChannelStatus(UUID id) {
	    return channelStatusRepository.findById(id)
	        .orElseThrow(() -> new RuntimeException("Channel not found")) ;
	}
}
