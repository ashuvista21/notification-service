package com.ashuvista21.notification.controller;

import java.util.Arrays;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ashuvista21.notification.dtos.ApiResponse;
import com.ashuvista21.notification.dtos.FCMToken;
import com.ashuvista21.notification.service.FCMTokenService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DeviceRegisterController {
	
	private final FCMTokenService fcmTokenService ;
	
	@PostMapping("/sms-gateway/register")
	public ResponseEntity<ApiResponse<Void>> registerSmsGateway(@RequestBody FCMToken fcmToken) {
		System.out.println(fcmToken.deviceName()) ;
		fcmTokenService.registerSMSGateway(fcmToken) ;
		
		return ResponseEntity.status(HttpStatus.OK)
	            .body(ApiResponse.<Void>builder()
	                    .message(Arrays.asList("SMS Gateway Registered Successfully"))
	                    .success(true)
	                    .status(HttpStatus.OK)
	                    .build()) ;
	}
}
