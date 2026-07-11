package com.ashuvista21.notification.service;

import java.util.UUID ;

public interface StatusService {
	void markSuccess(UUID channelStatusId) ;
	void markFailed(UUID channelStatusId, Exception ex) ;
	void markConfigMissing(UUID channelStatusId, Exception ex) ;
	void updateOverallStatus(UUID notificationId) ;
	void updateProviderMessageId(UUID channelStatusId, String correlationId) ;
}
