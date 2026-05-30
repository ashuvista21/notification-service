package com.ashuvista21.notification.dtos;

public record EventInbox(
		String eventId,
		boolean success,
		String errorMessage) {

}
