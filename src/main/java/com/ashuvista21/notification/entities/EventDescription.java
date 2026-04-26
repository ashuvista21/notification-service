package com.ashuvista21.notification.entities;

import java.util.UUID;

import com.ashuvista21.notification.enums.EventOutboxType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
		name = "event_description",
		indexes = {
		        @Index(name = "idx_user_channel", columnList = "userId, channel")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDescription {
	
	@Id
	private UUID correlationId ;
	
	@Enumerated(EnumType.STRING)
	private EventOutboxType eventCode ;
	private String eventDescription ;
	private UUID userId ;
}
