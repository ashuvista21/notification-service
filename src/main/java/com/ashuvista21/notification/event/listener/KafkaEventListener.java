package com.ashuvista21.notification.event.listener;

import java.util.Map ;
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
import com.ashuvista21.notification.dtos.OTPEvent ;
import com.ashuvista21.notification.enums.NotificationType ;
import com.ashuvista21.notification.service.EventOutboxService ;
import com.ashuvista21.notification.service.NotificationService ;
import com.ashuvista21.notification.service.StatusService ;
import com.ashuvista21.notification.utils.ValidatorUtils ;
import com.ashuvista21.notification.validator.NotificationValidator ;
import com.fasterxml.jackson.core.JsonProcessingException ;
import com.fasterxml.jackson.databind.ObjectMapper ;

import lombok.RequiredArgsConstructor ;

@Component
@RequiredArgsConstructor
public class KafkaEventListener {
	
	private final NotificationDispatcher notificationDispatcher ;
	private final NotificationService notificationService ;
	private final NotificationValidator notificationValidator ;
	private final StatusService statusService ;
	private final EventOutboxService eventOutboxService ;
	private final ObjectMapper mapper ;

	@KafkaListener(
            topics = "#{@notificationChannelProperties.dispatcherTopic}"
    )
    public void process(@Payload String payload, Acknowledgment ack) {
		
		NotificationEvent notificationEvent = deserialize(payload, NotificationEvent.class) ;
		
        notificationDispatcher.dispatch(notificationEvent.payload().toString()) ;

        ack.acknowledge() ;
    }
	
	@KafkaListener(
            topics = "#{@notificationChannelProperties.statusUpdateTopic}"
    )
    public void updateOverallStatus(@Payload String payload, Acknowledgment ack) {
		
		NotificationEvent notificationEvent = deserialize(payload, NotificationEvent.class) ;
		
		UUID notificationId = ValidatorUtils.validateUuidAndGetUuid(notificationEvent.payload().toString()) ;
		
        statusService.updateOverallStatus(notificationId) ;

        ack.acknowledge() ;
    }
	
	@KafkaListener(
			topics = "#{@notificationChannelProperties.inboundTopic}"
	)
	public void inboundNotification(@Payload String payload, Acknowledgment ack) {
		
		NotificationInboundEvent inboundEvent = deserialize(payload, NotificationInboundEvent.class) ;
		
		notificationValidator.validate(inboundEvent) ;
		
		NotificationCommand command = new NotificationCommand(
				inboundEvent.eventId(),
				UUID.fromString(inboundEvent.userId()), 
				NotificationType.valueOf(inboundEvent.eventType()),
				inboundEvent.eventContext(),
				inboundEvent.payload()) ;
		
		notificationService.createAndDispatch(command) ;
		
		ack.acknowledge() ;
	}
	
	@KafkaListener(
			topics = "#{@notificationChannelProperties.channelVerificationTopic}"
	)
	public void channelVerificationNotification(@Payload String payload, Acknowledgment ack) {
		
		//notificationValidator.validate(inboundEvent) ;
		OTPEvent otpEvent = deserialize(payload, OTPEvent.class) ;
		
		NotificationCommand command = new NotificationCommand(
				otpEvent.requestId(),
				UUID.fromString(otpEvent.userId()),
				NotificationType.valueOf(otpEvent.notificationType()),
				otpEvent.eventContext(),
				Map.of(
						"otp", otpEvent.otp(),
						"expiry", otpEvent.expiry(),
						"unit", otpEvent.unitTime())) ;
		
		notificationService.createAndDispatch(command) ;
		
		ack.acknowledge() ;
	}
	
	@KafkaListener(
			topics = "#{@notificationChannelProperties.eventInboxTopic}"
	)
	public void processInbox(@Payload String payload, Acknowledgment ack) {
		
		// This listener can be used for processing events that need to be stored in an inbox or for auditing purposes.
		// For now, we will just acknowledge the event without any processing.
		EventInbox eventInbox = deserialize(payload, EventInbox.class) ;
		
		UUID eventOutboxId = ValidatorUtils.validateUuidAndGetUuid(eventInbox.eventId()) ;
		
		if(eventInbox.success()) {
			eventOutboxService.markAsPublished(eventOutboxId) ;
		}
		else {
			eventOutboxService.markAsFailed(eventOutboxId) ;
		}
		
		ack.acknowledge() ;
	}
	
	private <T> T deserialize(String payload, Class<T> clazz) {
        try {
            return mapper.readValue(payload, clazz) ;
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(
                    "Failed to deserialize payload to "
                            + clazz.getSimpleName(),
                    e) ;
        }
    }
}
