package com.ashuvista21.notification.service.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ashuvista21.notification.dtos.FCMToken;
import com.ashuvista21.notification.entities.FCMDeviceInfo;
import com.ashuvista21.notification.repository.FCMTokenRepository;
import com.ashuvista21.notification.service.FCMTokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FCMTokenServiceImpl implements FCMTokenService {
	
	private final FCMTokenRepository fcmTokenRepository ;
	
	@Override
	@Transactional
	public void registerSMSGateway(FCMToken fcmToken) {
		fcmTokenRepository.deactivateTokens(fcmToken.deviceName()) ;
		
		FCMDeviceInfo fcmDeviceInfo = FCMDeviceInfo.builder()
				.id(UUID.randomUUID())
				.deviceName(fcmToken.deviceName())
				.fcmToken(fcmToken.fcmToken())
				.active(true)
				.createdAt(Instant.now())
				.build() ;
		
		fcmTokenRepository.save(fcmDeviceInfo) ;
	}
	
	@Override
	public String getSMSGatewayFCMToken(String deviceName) {
		return fcmTokenRepository.findByDeviceNameAndActiveTrue(deviceName)
				.map(FCMDeviceInfo::getFcmToken)
				.orElseThrow(() -> new RuntimeException("SMS Gateway not registered")) ;
	}
}
