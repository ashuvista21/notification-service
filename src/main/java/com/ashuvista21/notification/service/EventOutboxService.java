package com.ashuvista21.notification.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.ashuvista21.notification.entities.EventOutbox;

public interface EventOutboxService {
	void saveEvent(UUID eventId, String aggregateType, UUID aggregateId, String eventType, Object payload, UUID correlationId) ;
	List<EventOutbox> getPendingEvents(int limit) ;
	List<EventOutbox> getRetryableEvents(int maxRetries, int limit) ;
	void markAsPublished(UUID eventId) ;
	void markAsFailed(UUID eventId, String errorCode, String errorMeesage) ;
	void updateEvents(List<EventOutbox> events) ;
	void deleteProcessedEventsOlderThan(Instant cutoff) ;
}
