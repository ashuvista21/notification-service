package com.ashuvista21.notification.exceptions.utils;

import org.springframework.http.HttpStatus ;

import com.ashuvista21.notification.exceptions.NotificationUtilsException ;

public class InvalidUuidFormatException extends NotificationUtilsException {
	private static final long serialVersionUID = 1L ;
	
	public InvalidUuidFormatException(String message) {
		super(message, HttpStatus.BAD_REQUEST) ;
	}
}
