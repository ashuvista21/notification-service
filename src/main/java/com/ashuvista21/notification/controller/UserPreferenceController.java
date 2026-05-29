package com.ashuvista21.notification.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable ;
import org.springframework.web.bind.annotation.PutMapping ;
import org.springframework.web.bind.annotation.RequestMapping ;
import org.springframework.web.bind.annotation.RestController ;

import lombok.RequiredArgsConstructor ;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-preferences")
public class UserPreferenceController {
	
	@PutMapping("/{userId}")
	public void updateUserPreferences() {
		
	}
	
	@GetMapping("{userId}")
	public String getUserPreferences(@PathVariable String userId) {
		return null ;
	}
}
