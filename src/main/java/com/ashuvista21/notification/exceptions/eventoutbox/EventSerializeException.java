package com.ashuvista21.notification.exceptions.eventoutbox;

import org.springframework.http.HttpStatus;

import com.ashuvista21.notification.exceptions.EventOutboxException;

public class EventSerializeException extends EventOutboxException {

	private static final long serialVersionUID = 1L ;
	
	public EventSerializeException(String message) {
		super(message, HttpStatus.BAD_REQUEST) ;
	}

}
