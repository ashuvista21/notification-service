package com.ashuvista21.notification.dispatcher ;

import java.util.Map ;
import java.util.Set ;
import java.util.UUID ;
import java.util.concurrent.ExecutorService ;

import org.springframework.kafka.annotation.KafkaListener ;
import org.springframework.kafka.core.KafkaTemplate ;
import org.springframework.messaging.handler.annotation.Payload ;
import org.springframework.stereotype.Component ;
import org.springframework.transaction.annotation.Transactional ;

import com.ashuvista21.notification.channel.NotificationChannel ;
import com.ashuvista21.notification.config.NotificationChannelProperties ;
import com.ashuvista21.notification.config.NotificationChannelProperties.ChannelConfig ;
import com.ashuvista21.notification.dtos.ChannelPayload ;
import com.ashuvista21.notification.dtos.NotificationEvent ;
import com.ashuvista21.notification.entities.Notification ;
import com.ashuvista21.notification.entities.NotificationChannelStatus ;
import com.ashuvista21.notification.entities.UserChannelContact ;
import com.ashuvista21.notification.enums.NotificationCategory ;
import com.ashuvista21.notification.enums.NotificationChannelType ;
import com.ashuvista21.notification.enums.NotificationStatus ;
import com.ashuvista21.notification.enums.NotificationType ;
import com.ashuvista21.notification.enums.ProcessMode ;
import com.ashuvista21.notification.factory.BaseVariableResolverFactory ;
import com.ashuvista21.notification.factory.ChannelEnricherFactory ;
import com.ashuvista21.notification.factory.NotificationChannelFactory ;
import com.ashuvista21.notification.factory.NotificationTypeOverrideFactory ;
import com.ashuvista21.notification.repository.NotificationChannelStatusRepository ;
import com.ashuvista21.notification.repository.NotificationRepository ;
import com.ashuvista21.notification.service.UserChannelContactService ;
import com.ashuvista21.notification.service.UserNotificationCategoryPreferenceService ;
import com.ashuvista21.notification.service.UserNotificationTypePreferenceService ;

import lombok.RequiredArgsConstructor ;

@RequiredArgsConstructor
@Component
public class NotificationDispatcher {

	private final UserNotificationCategoryPreferenceService notificationCategoryPreferenceService ;
	private final UserNotificationTypePreferenceService notificationTypePreferenceService ;
	private final UserChannelContactService userContactService ;

	private final NotificationChannelFactory notificationChannelFactory ;
	private final BaseVariableResolverFactory baseVariableResolverFactory ;
	private final ChannelEnricherFactory channelEnricherFactory ;
	private final NotificationTypeOverrideFactory notificationTypeOverrideFactory ;

	private final NotificationRepository notificationRepository ;
	private final NotificationChannelStatusRepository channelStatusRepository ;

	private final ExecutorService notificationExecutor ;

	private final KafkaTemplate<String, NotificationEvent> kafkaTemplate ;
	private final String dispatcherTopic = "notification-dispatch" ;

	private final NotificationChannelProperties channelProperties ;

	private static final Map<NotificationCategory, Set<NotificationChannelType>> DEFAULT_CHANNELS = Map.of(
			NotificationCategory.OTP, Set.of(NotificationChannelType.SMS), 
			NotificationCategory.SECURITY, Set.of(NotificationChannelType.EMAIL, NotificationChannelType.SMS), 
			NotificationCategory.TRANSACTIONAL, Set.of(NotificationChannelType.EMAIL), 
			NotificationCategory.INFORMATION, Set.of(NotificationChannelType.EMAIL), 
			NotificationCategory.PROMOTIONAL, Set.of(NotificationChannelType.PUSH)) ;

	private static final ChannelConfig defaultChannelConfig = null ;

	@Transactional
	public void dispatch(Notification notification) {
		/*
		 * 
		 * 1. Receive EventDTO / POST Call 
		 * 2. Resolve channels (based on preferences + defaults) 
		 * 3. Create Notification 
		 * 		object status = PENDING 
		 * 		create NotificationChannelStatus entries (one per channel) 
		 * 4. Persist notification in DB 
		 * 5. Publish to dispatcher (or async process) 
		 * 6. For each channel: 
		 * 		get User Contact for channel
		 * 		build channel payload
		 * 		resolve payload variables based on 
		 * 			1. base notification category
		 * 			2  enrich with channel type
		 * 			3. override for notification type
		 * 		Call provider via factory 
		 * 		Update providerMessageId 
		 * 7. Update overall notification status
		 * 
		 */

		// Resolve channels (based on preferences + defaults)
		Set<NotificationChannelType> channels = resolveChannels(notification.getUserId(),
				notification.getNotificationType()) ;

		// Iterate through channels
		for (NotificationChannelType channel : channels) {

			// create channel status record
			NotificationChannelStatus channelStatus = NotificationChannelStatus.builder().notification(notification)
					.channelType(channel).status(NotificationStatus.CREATED).retryCount(0).build() ;

			notification.getChannels().add(channelStatus) ;
		}

		// Persist notification in DB
		notificationRepository.save(notification) ;

		NotificationEvent notificationEvent = new NotificationEvent("DISPATCH_NOTIFICATION",
				notification.getId().toString()) ;
		publish(dispatcherTopic, notification.getId().toString(), notificationEvent) ;
	}

	@KafkaListener(topics = dispatcherTopic)
	public void process(@Payload NotificationEvent notificationEvent) {

		UUID notificationId = UUID.fromString(notificationEvent.payload().toString()) ;

		Notification notification = notificationRepository.findById(notificationId)
				.orElseThrow(() -> new RuntimeException("Invalid Notification identifier : " + notificationId)) ;

		for (NotificationChannelStatus channelStatus : notification.getChannels()) {

			notificationExecutor.submit(() -> processChannel(channelStatus.getId())) ;
		}
	}

	private void processChannel(UUID notificationChannelStatusId) {

		NotificationChannelStatus channelStatus = getChannelStatus(notificationChannelStatusId) ;

		Notification notification = channelStatus.getNotification() ;

		NotificationChannel service = notificationChannelFactory.getChannel(channelStatus.getChannelType()) ;

		try {
			ChannelConfig config = channelProperties.getChannels().getOrDefault(channelStatus.getChannelType(),
					defaultChannelConfig) ;

			UserChannelContact userChannelContact = userContactService
					.getVerifiedUserChannelContact(notification.getUserId(), channelStatus.getChannelType()) ;
			
			Map<String, Object> variables = buildVariables(channelStatus) ;
			
			ChannelPayload payload = ChannelPayload.builder()
					.notificationId(notification.getId().toString())
					.channelId(channelStatus.getId().toString())
					.userId(notification.getUserId().toString())
					.notificationType(notification.getNotificationType().toString())
					.channelType(channelStatus.getChannelType().toString())
					.recipientAddress(userChannelContact.getValue())
					.variables(variables)
					.build() ;

			if (config.getProcessMode() == ProcessMode.ASYNC) {
				NotificationEvent event = new NotificationEvent("DISPATCH_CHANNEL", payload) ;
				publish(config.getTopic(), channelStatus.getId().toString(), event) ;
			} else
				service.send(payload) ;
			markSuccess(notificationChannelStatusId) ;
		} catch (Exception e) {
			markFailed(notificationChannelStatusId, e) ;
		}

		notification.setStatus(calculateOverallStatus(notification)) ;
	}

	private Set<NotificationChannelType> resolveChannels(UUID userId, NotificationType notificationType) {

		// 1. Check type override
		Set<NotificationChannelType> overrideChannels = notificationTypePreferenceService.getUserChannels(userId,
				notificationType) ;

		if (!overrideChannels.isEmpty()) {
			return overrideChannels ;
		}

		// 2. Fallback to category
		NotificationCategory category = notificationType.getCategory() ;

		Set<NotificationChannelType> categoryChannels = notificationCategoryPreferenceService.getUserChannels(userId,
				category) ;

		if (!categoryChannels.isEmpty()) {
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

	private void publish(String topic, String key, NotificationEvent event) {
		kafkaTemplate.send(topic, key.toString(), event).whenComplete((result, ex) -> {
			if (ex != null) {
				// Failure handling
				// log.error("Failed to publish notification: {}",
				// dispatcherDTO.getNotificationId(), ex);
			} else {
				// Success handling
				// log.info("Published notification: {} to partition: {}",
				// dispatcherDTO.getNotificationId(),
				// result.getRecordMetadata().partition());
			}
		}) ;
	}

	private Map<String, Object> buildVariables(NotificationChannelStatus channelStatus) {

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
		return channelStatusRepository.findById(id).orElseThrow(() -> new RuntimeException("Channel not found")) ;
	}
}
