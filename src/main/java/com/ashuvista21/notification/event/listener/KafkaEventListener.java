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
import com.ashuvista21.notification.exceptions.eventoutbox.EventDeserializeException ;
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
		
		// Skipping validation here since this event is produced internally and we can trust the format.
		// In a real-world scenario, you might want to add some basic validation or error handling here.
		NotificationEvent notificationEvent = deserialize(payload, NotificationEvent.class) ;
		
		// Assuming the payload contains the notificationId as a string, we validate and convert it to UUID.
		UUID notificationId = ValidatorUtils.validateUuidAndGetUuid(notificationEvent.payload().toString()) ;
		
        notificationDispatcher.dispatch(notificationId) ;

        ack.acknowledge() ;
    }
	
	@KafkaListener(
            topics = "#{@notificationChannelProperties.statusUpdateTopic}"
    )
    public void updateOverallStatus(@Payload String payload, Acknowledgment ack) {
		
		// Skipping validation here since this event is produced internally and we can trust the format.
		// In a real-world scenario, you might want to add some basic validation or error handling here.
		NotificationEvent notificationEvent = deserialize(payload, NotificationEvent.class) ;
		
		// Assuming the payload contains the notificationId as a string, we validate and convert it to UUID.
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
		
		// This listener can be used for processing channel verification events. For example, when a user adds a new notification channel (like email or SMS), we might want to send a verification code to that channel and listen for the verification event here.
		// Skipping validation here since this event is produced internally and we can trust the format.
		// In a real-world scenario, you might want to add some basic validation or error handling here.
		// We have validation while processing the OTP event, so we can skip it here.
		// Since all fields are strings, we can directly deserialize without worrying about type mismatches.
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
		// Skipping validation here since this event is produced internally and we can trust the format.
		EventInbox eventInbox = deserialize(payload, EventInbox.class) ;
		
		// Assuming the payload contains the eventId as a string, we validate and convert it to UUID.
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
            throw new EventDeserializeException(
                    "Failed to deserialize payload to "
                            + clazz.getSimpleName()) ;
        }
    }
}
