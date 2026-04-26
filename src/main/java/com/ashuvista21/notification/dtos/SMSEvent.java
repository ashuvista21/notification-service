package com.ashuvista21.notification.dtos;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SMSEvent {
	private String eventId ;
	private String recipientPhoneNumber ;
	private String template ;
	private Map<String, Object> variables ;
}
