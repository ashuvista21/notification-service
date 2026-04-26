package com.ashuvista21.notification.exceptions.eventdescription;

import org.springframework.http.HttpStatus;

import com.ashuvista21.notification.exceptions.EventDescriptionException;

public class EventDescriptionNotFoundException extends EventDescriptionException {

	private static final long serialVersionUID = 1L ;
	
	public EventDescriptionNotFoundException(String message) {
		super(message, HttpStatus.NOT_FOUND) ;
	}

}
