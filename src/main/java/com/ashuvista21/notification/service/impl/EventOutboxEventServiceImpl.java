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
import com.ashuvista21.notification.exceptions.eventoutbox.EventOutboxAlreadyExists;
import com.ashuvista21.notification.exceptions.eventoutbox.EventOutboxNotFoundException;
import com.ashuvista21.notification.repository.EventOutboxRepository;
import com.ashuvista21.notification.service.EventOutboxService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventOutboxEventServiceImpl implements EventOutboxService {
	
	private final EventOutboxRepository eventOutboxRepository ;
	private final ObjectMapper mapper ;

	@Override
	@Transactional
	public void saveEvent(UUID eventId, String aggregateType, UUID aggregateId, String eventType, Object payloadObject, UUID correlationId) {
		boolean alreadyExists = eventOutboxRepository.existsById(eventId) ;
		
		if(alreadyExists)
			throw new EventOutboxAlreadyExists("Event already exists with id " + eventId) ;
		
		String payload = "" ;
		try {
			payload = mapper.writeValueAsString(payloadObject);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Failed to serialize event payload", e) ;
		}
		
		EventOutbox eventOutbox = EventOutbox.builder()
			.eventId(eventId)
			.aggregateId(aggregateId)
			.aggregateType(eventType)
			.payload(payload)
			.correlationId(correlationId)
			.status(EventStatus.CREATED.toString())
			.build() ;
		
		eventOutboxRepository.save(eventOutbox) ;
	}

	@Override
	public List<EventOutbox> getPendingEvents(int limit) {
		Pageable pageable = PageRequest.of(0, limit) ;
		List<EventOutbox> events = eventOutboxRepository.findByStatusOrderByCreatedAtAsc(EventStatus.PENDING.toString(), pageable) ;
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
		
	}

	@Override
	@Transactional
	public void markAsFailed(UUID eventId, String errorCode, String errorMeesage) {
		EventOutbox event = eventOutboxRepository.findById(eventId)
				.orElseThrow(() -> new EventOutboxNotFoundException("Event not found with id " + eventId)) ;
		
		// dirty checking
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
