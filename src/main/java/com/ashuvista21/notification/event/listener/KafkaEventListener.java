package com.ashuvista21.notification.event.listener;

import java.util.UUID ;

import org.springframework.kafka.annotation.KafkaListener ;
import org.springframework.kafka.support.Acknowledgment ;
import org.springframework.messaging.handler.annotation.Payload ;
import org.springframework.stereotype.Component ;

import com.ashuvista21.notification.dispatcher.NotificationDispatcher ;
import com.ashuvista21.notification.dtos.EventInbox ;
import com.ashuvista21.notification.dtos.NotificationCommand ;
import com.ashuvista21.notification.dtos.NotificationEvent ;
import com.ashuvista21.notification.dtos.NotificationInboundEvent ;
import com.ashuvista21.notification.enums.NotificationType ;
import com.ashuvista21.notification.service.EventOutboxService ;
import com.ashuvista21.notification.service.NotificationService ;
import com.ashuvista21.notification.utils.ValidatorUtils ;
import com.ashuvista21.notification.validator.NotificationValidator ;

import lombok.RequiredArgsConstructor ;

@Component
@RequiredArgsConstructor
public class KafkaEventListener {
	
	private final NotificationDispatcher notificationDispatcher ;
	private final NotificationService notificationService ;
	private final NotificationValidator notificationValidator ;
	private final EventOutboxService eventOutboxService ;

	@KafkaListener(
            topics = "#{@notificationChannelProperties.dispatcherTopic}"
    )
    public void process(@Payload NotificationEvent notificationEvent, Acknowledgment ack) {

        notificationDispatcher.dispatch(notificationEvent.payload().toString()) ;

        ack.acknowledge() ;
    }
	
	@KafkaListener(
			topics = "#{@notificationChannelProperties.inboundTopic}"
	)
	public void inboundNotification(@Payload NotificationInboundEvent inboundEvent, Acknowledgment ack) {
		
		notificationValidator.validate(inboundEvent) ;
		
		NotificationCommand command = new NotificationCommand(
				inboundEvent.eventId(),
				UUID.fromString(inboundEvent.userId()), 
				NotificationType.valueOf(inboundEvent.eventType()),
				inboundEvent.payload()) ;
		
		notificationService.createAndDispatch(command) ;
		
		ack.acknowledge() ;
	}
	
	@KafkaListener(
			topics = "#{@notificationChannelProperties.eventInboxTopic}"
	)
	public void processInbox(@Payload EventInbox eventInbox, Acknowledgment ack) {
		
		// This listener can be used for processing events that need to be stored in an inbox or for auditing purposes.
		// For now, we will just acknowledge the event without any processing.
		UUID eventOutboxId = ValidatorUtils.validateUuidAndGetUuid(eventInbox.eventId()) ;
		
		if(eventInbox.success()) {
			eventOutboxService.markAsPublished(eventOutboxId) ;
		}
		else {
			eventOutboxService.markAsFailed(eventOutboxId) ;
		}
		
		ack.acknowledge() ;
	}
}
