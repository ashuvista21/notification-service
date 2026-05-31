package com.ashuvista21.notification.exceptions;

import org.springframework.http.HttpStatus ;

public class NotificationChannelException extends RuntimeException {

	private static final long serialVersionUID = 1L ;
	private final HttpStatus status ;
	
	public NotificationChannelException(String message, HttpStatus status) {
		super(message) ;
		this.status = status ;
	}
	
	public NotificationChannelException(String message, HttpStatus status, Throwable cause) {
		super(message, cause) ;
		this.status = status ;
	}
	
	public HttpStatus getStatus() {
		return status ;
	}

}
