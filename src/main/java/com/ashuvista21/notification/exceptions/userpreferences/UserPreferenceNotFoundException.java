package com.ashuvista21.notification.exceptions.userpreferences;

import org.springframework.http.HttpStatus;

import com.ashuvista21.notification.exceptions.UserPreferenceException;

public class UserPreferenceNotFoundException extends UserPreferenceException {

	private static final long serialVersionUID = 1L ;
	
	public UserPreferenceNotFoundException(String message) {
		super(message, HttpStatus.NOT_FOUND) ;
	}

}
