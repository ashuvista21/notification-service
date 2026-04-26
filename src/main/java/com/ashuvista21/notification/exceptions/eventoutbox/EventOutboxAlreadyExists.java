package com.ashuvista21.notification.exceptions.eventoutbox;

import org.springframework.http.HttpStatus;

import com.ashuvista21.notification.exceptions.EventOutboxException;

public class EventOutboxAlreadyExists extends EventOutboxException {

	private static final long serialVersionUID = 1L ;
	
	public EventOutboxAlreadyExists(String message) {
		super(message, HttpStatus.CONFLICT) ;
	}

}
