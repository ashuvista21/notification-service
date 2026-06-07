package com.ashuvista21.notification.validator.impl;

import org.springframework.stereotype.Service ;

import com.ashuvista21.notification.dtos.NotificationInboundEvent ;
import com.ashuvista21.notification.dtos.NotificationRequest ;
import com.ashuvista21.notification.exceptions.notification.DuplicateNotificationRequestException ;
import com.ashuvista21.notification.repository.NotificationRepository ;
import com.ashuvista21.notification.utils.ValidatorUtils ;
import com.ashuvista21.notification.validator.NotificationValidator ;

import lombok.RequiredArgsConstructor ;

@Service
@RequiredArgsConstructor
public class NotificationValidatorImpl implements NotificationValidator {
	
	private final NotificationRepository notificationRepository ;
	
	
	@Override
	public void validate(NotificationRequest request) {
		validateEventId(request.getEventId()) ;
		validateEventType(request.getEventType()) ;
		validateUser(request.getUserId()) ;
	}

	@Override
	public void validate(NotificationInboundEvent event) {
		validateEventId(event.eventId()) ;
		validateEventType(event.eventType()) ;
		validateUser(event.userId()) ;
	}
	
	private void validateEventId(String eventId) {
        boolean exists = notificationRepository.existsByUserEventRef(eventId) ;

        if(exists) {
            throw new DuplicateNotificationRequestException("Notification request with eventId " + eventId + " already exists.") ;
        }
    }

    private void validateEventType(String eventType) {
        ValidatorUtils.validateNotificationTypeOrThrow(eventType) ;
    }

    private void validateUser(String userId) {
    	ValidatorUtils.validateUuidAndGetUuid(userId) ;
    }
}
