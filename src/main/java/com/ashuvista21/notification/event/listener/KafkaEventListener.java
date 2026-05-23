package com.ashuvista21.notification.event.listener;

import java.util.UUID ;

import org.springframework.kafka.annotation.KafkaListener ;
import org.springframework.kafka.support.Acknowledgment ;
import org.springframework.messaging.handler.annotation.Payload ;
import org.springframework.stereotype.Component ;

import com.ashuvista21.notification.dispatcher.NotificationDispatcher ;
import com.ashuvista21.notification.dtos.NotificationCommand ;
import com.ashuvista21.notification.dtos.NotificationEvent ;
import com.ashuvista21.notification.dtos.NotificationInboundEvent ;
import com.ashuvista21.notification.enums.NotificationType ;
import com.ashuvista21.notification.service.NotificationService ;
import com.ashuvista21.notification.validator.NotificationValidator ;

import lombok.RequiredArgsConstructor ;

@Component
@RequiredArgsConstructor
public class KafkaEventListener {
	
	private final NotificationDispatcher notificationDispatcher ;
	private final NotificationService notificationService ;
	private final NotificationValidator notificationValidator ;

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
}
