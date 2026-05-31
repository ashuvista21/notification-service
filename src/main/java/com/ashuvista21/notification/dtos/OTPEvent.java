package com.ashuvista21.notification.dtos;

public record OTPEvent(
		String requestId,
		String otp,
		String expiry,
		String unitTime,
		String userId,
		String notificationType,
		String eventContext) {
}
