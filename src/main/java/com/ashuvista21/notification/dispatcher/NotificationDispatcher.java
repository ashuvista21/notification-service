package com.ashuvista21.notification.dispatcher ;

import java.util.UUID ;
import java.util.concurrent.ExecutorService ;

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
import com.ashuvista21.notification.service.EventOutboxService ;
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

	private final NotificationChannelProperties channelProperties ;
	
	private final EventOutboxService eventOutboxService ;

	public void dispatch(String aggregateId) {

		UUID notificationId = UUID.fromString(aggregateId) ;

		Notification notification = getNotificationEntity(notificationId) ;

		for (NotificationChannelStatus channelStatus : notification.getChannels()) {
			notificationExecutor.submit(() -> processChannel(channelStatus.getId())) ;
		}
	}
	
	@Transactional
	private void processChannel(UUID notificationChannelStatusId) {

		NotificationChannelStatus channelStatus = getChannelStatus(notificationChannelStatusId) ;

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

	private String publish(String topic, String key, NotificationEvent event) {
		return eventOutboxService.createEvent(event.originator(), key, topic, event) ;
	}

	private String publishAsync(NotificationChannelStatus channelStatus, ChannelPayload payload, ChannelConfig config) {
		NotificationEvent event = new NotificationEvent("NOTIFICATION_CHANNEL", payload) ;
		return publish(config.getTopic(), channelStatus.getId().toString(), event) ;
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
