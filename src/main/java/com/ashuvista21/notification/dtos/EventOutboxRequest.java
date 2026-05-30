package com.ashuvista21.notification.dtos;

public record EventOutboxRequest(
		boolean success,
		String errorMessage) {

}
