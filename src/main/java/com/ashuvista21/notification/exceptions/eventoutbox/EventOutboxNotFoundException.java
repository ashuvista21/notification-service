package com.ashuvista21.notification.exceptions.eventoutbox;

import org.springframework.http.HttpStatus;

import com.ashuvista21.notification.exceptions.EventOutboxException;

public class EventOutboxNotFoundException extends EventOutboxException {

	private static final long serialVersionUID = 1L ;
	
	public EventOutboxNotFoundException(String message) {
		super(message, HttpStatus.NOT_FOUND) ;
	}

}
