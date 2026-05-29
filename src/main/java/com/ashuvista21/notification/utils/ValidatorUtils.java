package com.ashuvista21.notification.utils;

import java.util.UUID ;

import com.ashuvista21.notification.enums.NotificationChannelType ;

public class ValidatorUtils {
	public static UUID validateUuidAndGetUuid(String uuidStr) {
		try {
			return UUID.fromString(uuidStr) ;
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid UUID format : " + uuidStr) ;
		}
	}
	
	public static NotificationChannelType validateChannelOrThrow(String channelStr) {
		try {
			return NotificationChannelType.valueOf(channelStr) ;
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Unsupported " + channelStr + " channel") ;
		}
	}
	
	public static void validateEmailFormat(String email) {
		String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$" ;
		if (!email.matches(emailRegex)) {
			throw new IllegalArgumentException("Invalid email format : " + email) ;
		}
	}
	
	public static void validatePhoneNumberFormat(String phoneNumber) {
		String phoneRegex = "^\\+?[0-9]{10,15}$" ;
		if (!phoneNumber.matches(phoneRegex)) {
			throw new IllegalArgumentException("Invalid phone number format : " + phoneNumber) ;
		}
	}
}
