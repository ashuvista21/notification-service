package com.ashuvista21.notification.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ashuvista21.notification.entities.EventDescription;
import com.ashuvista21.notification.enums.EventOutboxType;
import com.ashuvista21.notification.exceptions.eventdescription.EventDescriptionNotFoundException;
import com.ashuvista21.notification.repository.EventDescriptionRepository;
import com.ashuvista21.notification.service.EventDescriptionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventDescriptionServiceImpl implements EventDescriptionService {
	
	private final EventDescriptionRepository eventDescriptionRepository ;

	@Override
	@Transactional
	public UUID saveEventDescription(EventOutboxType eventCode, String description, UUID userId) {
		EventDescription eventDescription = EventDescription.builder()
				.correlationId(UUID.randomUUID())
				.eventCode(eventCode)
				.eventDescription(description)
				.userId(userId)
				.build() ;
		
		eventDescriptionRepository.save(eventDescription) ;
		
		return eventDescription.getCorrelationId() ;
		
	}

	@Override
	public List<EventDescription> getAllUserEventsDescritpionByEventCode(String eventCode, UUID userId) {
		return eventDescriptionRepository.findByEventCodeAndUserId(eventCode, userId) ;
	}

	@Override
	public EventDescription getEventsDescritpionByCorrelationId(UUID correlationId) {
		return eventDescriptionRepository.findById(correlationId)
				.orElseThrow(() -> new EventDescriptionNotFoundException("Invalid event correltion id") ) ;
	}

}
