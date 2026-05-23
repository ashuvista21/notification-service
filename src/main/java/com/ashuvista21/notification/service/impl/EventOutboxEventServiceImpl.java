package com.ashuvista21.notification.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ashuvista21.notification.entities.EventOutbox;
import com.ashuvista21.notification.enums.EventStatus;
import com.ashuvista21.notification.exceptions.eventoutbox.EventOutboxNotFoundException;
import com.ashuvista21.notification.repository.EventOutboxRepository;
import com.ashuvista21.notification.service.EventOutboxService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator ;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventOutboxEventServiceImpl implements EventOutboxService {
	
	private final EventOutboxRepository eventOutboxRepository ;
	private final ObjectMapper mapper ;

	@Override
	@Transactional
	public String createEvent(String aggregateType, String aggregateId, String topic, Object payloadObject) {
		UUID eventId = UuidCreator.getTimeOrdered() ;
		
		String payload = "" ;
		try {
			payload = mapper.writeValueAsString(payloadObject);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Failed to serialize event payload", e) ;
		}
		
		EventOutbox eventOutbox = EventOutbox.builder()
			.eventId(eventId)
			.aggregateId(aggregateId)
			.aggregateType(aggregateType)
			.topic(topic)
			.payload(payload)
			.status(EventStatus.UNPROCESSED.toString())
			.build() ;
		
		eventOutboxRepository.save(eventOutbox) ;
		
		return eventId.toString() ;
	}

	@Override
	@Transactional
	public List<EventOutbox> getUnprocessedEvents(int limit) {
		Pageable pageable = PageRequest.of(0, limit) ;
		List<EventOutbox> events = eventOutboxRepository
				.findByStatusOrderByCreatedAtAsc(EventStatus.UNPROCESSED.toString(), pageable) ;
		
		events.forEach(event -> 
			event.setStatus(
				EventStatus.PROCESSING.toString()
        	)
		) ;
		
		return events ;
	}

	@Override
	public List<EventOutbox> getRetryableEvents(int maxRetries, int limit) {
		Pageable pageable = PageRequest.of(0, limit) ;
		List<EventOutbox> events = eventOutboxRepository.findByStatusOrderByCreatedAtAsc(EventStatus.RETRY.toString(), pageable) ;
		return events.stream()
				.filter(event -> event.getRetryCount() <= 3)
				.toList() ;
	}

	@Override
	@Transactional
	public void markAsPublished(UUID eventId) {
		EventOutbox event = eventOutboxRepository.findById(eventId)
				.orElseThrow(() -> new EventOutboxNotFoundException("Event not found with id " + eventId)) ;
		
		// dirty checking
		event.setStatus(EventStatus.PUBLISHED.toString()) ;
		event.setProcessedAt(Instant.now()) ;
		
	}

	@Override
	@Transactional
	public void markAsFailed(UUID eventId) {
		EventOutbox event = eventOutboxRepository.findById(eventId)
				.orElseThrow(() -> new EventOutboxNotFoundException("Event not found with id " + eventId)) ;
		
		// dirty checking
		event.setRetryCount(
                event.getRetryCount() + 1
        ) ;

        if(event.getRetryCount() > 5) {
            event.setStatus(EventStatus.MAX_RETRY.toString()) ;
        }
        else
        	event.setStatus(EventStatus.FAIL.toString()) ;
	}

	@Override
	public void updateEvents(List<EventOutbox> events) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteProcessedEventsOlderThan(Instant cutoff) {
		// TODO Auto-generated method stub
		
	}

}
