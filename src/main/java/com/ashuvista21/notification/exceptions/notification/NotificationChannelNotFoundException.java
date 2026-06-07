package com.ashuvista21.notification.exceptions.notification;

import org.springframework.http.HttpStatus ;

import com.ashuvista21.notification.exceptions.NotificationException ;

public class NotificationChannelNotFoundException extends NotificationException {
	private static final long serialVersionUID = 1L ;
	
	public NotificationChannelNotFoundException(String message) {
		super(message, HttpStatus.NOT_FOUND) ;
	}
}
