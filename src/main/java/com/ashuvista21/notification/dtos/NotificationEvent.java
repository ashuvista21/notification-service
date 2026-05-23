package com.ashuvista21.notification.dtos;

public record NotificationEvent(
		String originator,
		Object payload) {

}
