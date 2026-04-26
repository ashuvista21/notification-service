package com.ashuvista21.notification.service;

import com.ashuvista21.notification.dtos.FCMToken;

public interface FCMTokenService {
	void registerSMSGateway(FCMToken fcmToken) ;
	String getSMSGatewayFCMToken(String deviceName) ;
}
