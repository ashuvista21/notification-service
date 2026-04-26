package com.ashuvista21.notification.service;

import java.util.List;
import java.util.UUID;

import com.ashuvista21.notification.entities.EventDescription;
import com.ashuvista21.notification.enums.EventOutboxType;

public interface EventDescriptionService {
	UUID saveEventDescription(EventOutboxType eventCode, String description, UUID userId) ;
	List<EventDescription> getAllUserEventsDescritpionByEventCode(String eventCode, UUID userId) ;
	EventDescription getEventsDescritpionByCorrelationId(UUID correlationId) ;
}
