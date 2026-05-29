package com.ashuvista21.notification.dtos;

public record UserChannelContactView(
		String channel,
		String value,
		boolean verfied,
		boolean enabled) {

}
