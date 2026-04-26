package com.ashuvista21.notification.dtos;

import java.time.Instant;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailEvent {
        String eventId ;				// Unique ID (UUID)
        String eventType ;				// e.g., USER_REGISTRATION, PASSWORD_RESET
        String recipient ;				// Target email address
        String template ;				// Email template identifier
        Map<String, Object> variables ;	// Template placeholders (e.g., {"userName": "Ashutosh"})
        Instant timestamp ;				// When the event was created
}
