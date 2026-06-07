package com.ashuvista21.notification.exceptions.eventoutbox;

import org.springframework.http.HttpStatus;

import com.ashuvista21.notification.exceptions.EventOutboxException;

public class EventOutboxAlreadyExistsException extends EventOutboxException {

	private static final long serialVersionUID = 1L ;
	
	public EventOutboxAlreadyExistsException(String message) {
		super(message, HttpStatus.CONFLICT) ;
	}

}
