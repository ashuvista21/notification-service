package com.ashuvista21.notification.exceptions.channels;

import org.springframework.http.HttpStatus ;

import com.ashuvista21.notification.exceptions.NotificationChannelException ;

public class SmsCarrierRestrictionException extends NotificationChannelException{

	private static final long serialVersionUID = 1L ;
	
	public SmsCarrierRestrictionException(String message) {
		super(message, HttpStatus.BAD_GATEWAY) ;
	}
	
}
