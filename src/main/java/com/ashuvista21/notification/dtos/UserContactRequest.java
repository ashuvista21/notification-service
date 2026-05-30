package com.ashuvista21.notification.dtos;

public record UserContactRequest(
		String channel,
		String value,
		boolean createIfAbsent,
		boolean overrideIfPresent
		) {

}
