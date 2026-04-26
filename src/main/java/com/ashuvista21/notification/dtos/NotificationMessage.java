package com.ashuvista21.notification.dtos;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.ashuvista21.notification.enums.NotificationChannelType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationMessage {
	private UUID eventId ;
	private UUID userId ;
	private List<NotificationChannelType> channels ;
	private String eventType ;
	private String templateName ;
	private Map<String, Object> variables ;
	private Instant timestamp ;
}
