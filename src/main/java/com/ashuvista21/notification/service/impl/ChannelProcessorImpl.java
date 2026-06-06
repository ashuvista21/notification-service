package com.ashuvista21.notification.service.impl;

import java.util.UUID ;

import org.springframework.stereotype.Service ;

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
import com.ashuvista21.notification.service.ChannelProcessor ;
import com.ashuvista21.notification.service.EventOutboxService ;
import com.ashuvista21.notification.service.PayloadBuilderService ;
import com.ashuvista21.notification.service.StatusService ;

import lombok.RequiredArgsConstructor ;

@Service
@RequiredArgsConstructor
public class ChannelProcessorImpl implements ChannelProcessor {
	
	private final NotificationChannelStatusRepository channelStatusRepository ;
	private final NotificationRepository notificationRepository ;
	private final NotificationChannelProperties channelProperties ;
	private final PayloadBuilderService payloadBuilderService ;
	private final EventOutboxService eventOutboxService ;
	private final StatusService statusService ;
	private final NotificationChannelFactory notificationChannelFactory ;
	
	@Override
	public void processChannel(UUID notificationChannelStatusId, UUID notificationId) {
		
		NotificationChannelStatus channelStatus = channelStatusRepository
				.findById(notificationChannelStatusId)
				.orElseThrow(() -> new RuntimeException("Channel not found")) ;
		
		Notification notification = notificationRepository
				.findById(notificationId)
				.orElseThrow(() -> new RuntimeException("Notification not found")) ;
		
		try {
			ChannelConfig config = channelProperties.getChannels().getOrDefault(channelStatus.getChannelType(),
					channelProperties.getDefaultConfig()) ;

			ChannelPayload payload = payloadBuilderService.buildPayload(channelStatus, notification) ;

			if (config.getProcessMode() == ProcessMode.ASYNC) {
				String correlationId = publishAsync(channelStatus, payload, config) ;
				statusService.updateProviderMessageId(notificationChannelStatusId, correlationId) ;
			} else {
				sendSync(channelStatus, payload) ;
			}
			statusService.markSuccess(notificationChannelStatusId) ;
			
		} catch (Exception e) {
			statusService.markFailed(notificationChannelStatusId, e) ;
		} finally {
			// Update overall notification status in async way to avoid blocking channel processing
			// Publish event to update overall notification status
			NotificationEvent event = new NotificationEvent("NOTIFICATION_STATUS_UPDATE", notificationId.toString()) ;
			
			publish(channelProperties.getStatusUpdateTopic(), notificationId.toString(), event) ;
		}
	}
	
	private String publishAsync(NotificationChannelStatus channelStatus, ChannelPayload payload, ChannelConfig config) {
		NotificationEvent event = new NotificationEvent("NOTIFICATION_CHANNEL", payload) ;
		return publish(config.getTopic(), channelStatus.getId().toString(), event) ;
	}
	
	private void sendSync(NotificationChannelStatus channelStatus, ChannelPayload payload) {
		NotificationChannel service = notificationChannelFactory.getChannel(channelStatus.getChannelType()) ;
		service.send(payload) ;
	}
	
	private String publish(String topic, String key, NotificationEvent event) {
		return eventOutboxService.createEvent(event.originator(), key, topic, event) ;
	}
}
