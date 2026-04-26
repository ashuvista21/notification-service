package com.ashuvista21.notification.exceptions.userchannelcontact;

import org.springframework.http.HttpStatus;

import com.ashuvista21.notification.exceptions.UserChannelContactException;

public class UserChannelContactAlreadyVerifiedException extends UserChannelContactException {

	private static final long serialVersionUID = 1L ;
	
	public UserChannelContactAlreadyVerifiedException(String message) {
		super(message, HttpStatus.CONFLICT) ;
	}
}
