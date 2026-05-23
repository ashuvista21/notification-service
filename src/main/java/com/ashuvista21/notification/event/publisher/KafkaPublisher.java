package com.ashuvista21.notification.event.publisher;

import java.util.List ;

import org.springframework.kafka.core.KafkaTemplate ;
import org.springframework.scheduling.annotation.Scheduled ;
import org.springframework.stereotype.Component ;
import org.springframework.transaction.annotation.Transactional ;

import com.ashuvista21.notification.entities.EventOutbox ;
import com.ashuvista21.notification.service.EventOutboxService ;

import lombok.RequiredArgsConstructor ;

@Transactional
@RequiredArgsConstructor
@Component
public class KafkaPublisher {
	
	private final KafkaTemplate<String, String> kafkaTemplate ;
	private final EventOutboxService eventOutboxService ;
	
	@Scheduled(fixedDelay = 5000)
	public void publishEvents() {

	    List<EventOutbox> events =
	            eventOutboxService.getUnprocessedEvents(10) ;
	    
	    if(events.isEmpty()) {
	        //log.debug("No pending outbox events");
	        return ;
	    }

	    for (EventOutbox event : events) {

	        kafkaTemplate.send(
	                event.getTopic(),
	                event.getAggregateId(),
	                event.getPayload()
	        ).whenComplete((result, ex) -> {
	            if(ex != null) {
	                eventOutboxService.markAsFailed(event.getEventId()) ;
	            } else {
	            	eventOutboxService.markAsPublished(event.getEventId()) ;
	            }
	        }) ;
	    }
	}
}
