package com.ashuvista21.notification.dtos;

import java.util.UUID;

public record ContactVerificationEvent(
		UUID eventId,
	    UUID userId,
	    String eventType,
	    String templateType,
	    UUID correlationId
) {}
