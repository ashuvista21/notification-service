package com.ashuvista21.notification.utils;

import java.util.UUID ;

import com.ashuvista21.notification.enums.NotificationCategory ;
import com.ashuvista21.notification.enums.NotificationChannelType ;
import com.ashuvista21.notification.enums.NotificationType ;
import com.ashuvista21.notification.exceptions.utils.InvalidContactValueFormatException ;
import com.ashuvista21.notification.exceptions.utils.InvalidNotificationCategoryException ;
import com.ashuvista21.notification.exceptions.utils.InvalidNotificationChannelException ;
import com.ashuvista21.notification.exceptions.utils.InvalidNotificationTypeException ;
import com.ashuvista21.notification.exceptions.utils.InvalidUuidFormatException ;

public class ValidatorUtils {
	public static UUID validateUuidAndGetUuid(String uuidStr) {
		try {
			return UUID.fromString(uuidStr) ;
		} catch (IllegalArgumentException e) {
			throw new InvalidUuidFormatException("Invalid UUID format : " + uuidStr) ;
		}
	}
	
	public static NotificationChannelType validateChannelOrThrow(String channelStr) {
		try {
			return NotificationChannelType.valueOf(channelStr) ;
		} catch (IllegalArgumentException e) {
			throw new InvalidNotificationChannelException("Unsupported " + channelStr + " channel") ;
		}
	}
	
	public static void validateEmailFormat(String email) {
		String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$" ;
		if (!email.matches(emailRegex)) {
			throw new InvalidContactValueFormatException("Invalid email format : " + email) ;
		}
	}
	
	public static void validatePhoneNumberFormat(String phoneNumber) {
		String phoneRegex = "^\\+?[0-9]{10,15}$" ;
		if (!phoneNumber.matches(phoneRegex)) {
			throw new InvalidContactValueFormatException("Invalid phone number format : " + phoneNumber) ;
		}
	}
	
	public static NotificationCategory validateCategoryOrThrow(String categoryStr) {
		try {
			return NotificationCategory.valueOf(categoryStr) ;
		} catch (IllegalArgumentException e) {
			throw new InvalidNotificationCategoryException("Unsupported " + categoryStr + " category") ;
		}
	}
	
	public static NotificationType validateNotificationTypeOrThrow(String typeStr) {
		try {
			return NotificationType.valueOf(typeStr) ;
		} catch (IllegalArgumentException e) {
			throw new InvalidNotificationTypeException("Unsupported " + typeStr + " notification type") ;
		}
	}
}
