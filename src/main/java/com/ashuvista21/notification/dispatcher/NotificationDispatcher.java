package com.ashuvista21.notification.dispatcher ;

import java.util.UUID ;
import java.util.concurrent.ExecutorService ;

import org.springframework.kafka.annotation.KafkaListener ;
import org.springframework.kafka.core.KafkaTemplate ;
import org.springframework.kafka.support.Acknowledgment ;
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
import com.ashuvista21.notification.enums.ProcessMode ;
import com.ashuvista21.notification.factory.NotificationChannelFactory ;
import com.ashuvista21.notification.repository.NotificationChannelStatusRepository ;
import com.ashuvista21.notification.repository.NotificationRepository ;
import com.ashuvista21.notification.service.PayloadBuilderService ;
import com.ashuvista21.notification.service.StatusService ;

import lombok.RequiredArgsConstructor ;

@RequiredArgsConstructor
@Component
public class NotificationDispatcher {

	private final NotificationChannelFactory notificationChannelFactory ;
	
	private final PayloadBuilderService payloadBuilderService ;
	private final StatusService statusService ;

	private final NotificationRepository notificationRepository ;
	private final NotificationChannelStatusRepository channelStatusRepository ;

	private final ExecutorService notificationExecutor ;

	private final KafkaTemplate<String, NotificationEvent> kafkaTemplate ;
	private final String dispatcherTopic = "notification-dispatch" ;

	private final NotificationChannelProperties channelProperties ;

	@KafkaListener(topics = dispatcherTopic)
	public void process(@Payload NotificationEvent notificationEvent, Acknowledgment ack) {

		UUID notificationId = UUID.fromString(notificationEvent.payload().toString()) ;

		Notification notification = getNotificationEntity(notificationId) ;
		
		ack.acknowledge() ;

		for (NotificationChannelStatus channelStatus : notification.getChannels()) {
			notificationExecutor.submit(() -> processChannel(channelStatus.getId())) ;
		}
	}

	private void processChannel(UUID notificationChannelStatusId) {

		NotificationChannelStatus channelStatus = getChannelStatus(notificationChannelStatusId) ;

		try {
			ChannelConfig config = channelProperties.getChannels().getOrDefault(channelStatus.getChannelType(),
					channelProperties.getDefaultConfig()) ;
			
			ChannelPayload payload = payloadBuilderService.buildPayload(channelStatus) ;

			if (config.getProcessMode() == ProcessMode.ASYNC) {
				publishAsync(channelStatus, payload, config) ;
			} else
				sendSync(channelStatus, payload) ;
			statusService.markSuccess(notificationChannelStatusId) ;
			
		} catch (Exception e) {
			statusService.markFailed(notificationChannelStatusId, e) ;
		}

		statusService.updateOverallStatus(channelStatus.getNotification().getId()) ;
	}
	
	public void publishNotification(String key, NotificationEvent event) {
		publish(dispatcherTopic, key, event) ;
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

	private void publishAsync(NotificationChannelStatus channelStatus, ChannelPayload payload, ChannelConfig config) {
		NotificationEvent event = new NotificationEvent("DISPATCH_CHANNEL", payload) ;
		publish(config.getTopic(), channelStatus.getId().toString(), event) ;
	}
	
	private void sendSync(NotificationChannelStatus channelStatus, ChannelPayload payload) {
		NotificationChannel service = notificationChannelFactory.getChannel(channelStatus.getChannelType()) ;
		service.send(payload) ;
	}

	@Transactional
	public NotificationChannelStatus getChannelStatus(UUID id) {
		return channelStatusRepository.findById(id).orElseThrow(() -> new RuntimeException("Channel not found")) ;
	}
	
	@Transactional
	public Notification getNotificationEntity(UUID id) {
		return notificationRepository.findById(id).orElseThrow(() -> new RuntimeException("Notification not found")) ;
	}
}
