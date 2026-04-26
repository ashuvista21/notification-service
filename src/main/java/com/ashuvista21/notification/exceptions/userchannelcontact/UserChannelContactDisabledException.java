package com.ashuvista21.notification.exceptions.userchannelcontact;

import org.springframework.http.HttpStatus;

import com.ashuvista21.notification.exceptions.UserChannelContactException;

public class UserChannelContactDisabledException extends UserChannelContactException {

	private static final long serialVersionUID = 1L ;
	
	public UserChannelContactDisabledException(String message) {
		super(message, HttpStatus.FORBIDDEN) ;
	}

}
