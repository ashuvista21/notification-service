package com.ashuvista21.notification.service.impl;

import java.util.UUID ;

import org.springframework.stereotype.Service ;
import org.springframework.transaction.annotation.Transactional ;

import com.ashuvista21.notification.channel.NotificationChannel ;
import com.ashuvista21.notification.config.NotificationChannelProperties ;
import com.ashuvista21.notification.config.NotificationChannelProperties.ChannelConfig ;
import com.ashuvista21.notification.dtos.ChannelPayload ;
import com.ashuvista21.notification.dtos.NotificationEvent ;
import com.ashuvista21.notification.entities.NotificationChannelStatus ;
import com.ashuvista21.notification.enums.ProcessMode ;
import com.ashuvista21.notification.factory.NotificationChannelFactory ;
import com.ashuvista21.notification.repository.NotificationChannelStatusRepository ;
import com.ashuvista21.notification.service.ChannelProcessor ;
import com.ashuvista21.notification.service.EventOutboxService ;
import com.ashuvista21.notification.service.PayloadBuilderService ;
import com.ashuvista21.notification.service.StatusService ;

import lombok.RequiredArgsConstructor ;

@Service
@RequiredArgsConstructor
public class ChannelProcessorImpl implements ChannelProcessor {
	
	private final NotificationChannelStatusRepository channelStatusRepository ;
	private final NotificationChannelProperties channelProperties ;
	private final PayloadBuilderService payloadBuilderService ;
	private final EventOutboxService eventOutboxService ;
	private final StatusService statusService ;
	private final NotificationChannelFactory notificationChannelFactory ;
	
	@Transactional
	@Override
	public void processChannel(UUID notificationChannelStatusId) {

		NotificationChannelStatus channelStatus = channelStatusRepository
				.findById(notificationChannelStatusId)
				.orElseThrow(() -> new RuntimeException("Channel not found")) ;

		try {
			ChannelConfig config = channelProperties.getChannels().getOrDefault(channelStatus.getChannelType(),
					channelProperties.getDefaultConfig()) ;

			ChannelPayload payload = payloadBuilderService.buildPayload(channelStatus) ;

			if (config.getProcessMode() == ProcessMode.ASYNC) {
				String correlationId = publishAsync(channelStatus, payload, config) ;
				channelStatus.setProviderMessageId(correlationId) ;
			} else
				sendSync(channelStatus, payload) ;

			statusService.markSuccess(notificationChannelStatusId) ;
			
		} catch (Exception e) {
			statusService.markFailed(notificationChannelStatusId, e) ;
		}

		statusService.updateOverallStatus(channelStatus.getNotification().getId()) ;
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
