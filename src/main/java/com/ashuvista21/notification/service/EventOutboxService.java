package com.ashuvista21.notification.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.ashuvista21.notification.entities.EventOutbox;

public interface EventOutboxService {
	String createEvent(String aggregateType, String aggregateId, String topic, Object payload) ;
	List<EventOutbox> getUnprocessedEvents(int limit) ;
	List<EventOutbox> getRetryableEvents(int maxRetries, int limit) ;
	void markAsPublished(UUID eventId) ;
	void markAsFailed(UUID eventId) ;
	void updateEvents(List<EventOutbox> events) ;
	void deleteProcessedEventsOlderThan(Instant cutoff) ;
}
