package com.ashuvista21.notification.validator.impl;

import java.util.UUID ;

import org.springframework.stereotype.Service ;

import com.ashuvista21.notification.dtos.NotificationInboundEvent ;
import com.ashuvista21.notification.dtos.NotificationRequest ;
import com.ashuvista21.notification.enums.NotificationType ;
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
		validateUser(request.getUserId()) ;
		validateEventId(
				request.getEventId(),
				validateEventType(request.getEventType()),
				validateUser(request.getUserId())) ;
	}

	@Override
	public void validate(NotificationInboundEvent event) {
		validateEventId(
				event.eventId(),
				validateEventType(event.eventType()),
				validateUser(event.userId())) ;
	}
	
	private void validateEventId(String eventId, NotificationType type, UUID userId) {
		if(!type.getIdempotencyFlag())
			return ;
        boolean exists = notificationRepository.existsByNotificationTypeAndUserIdAndUserEventRef(type, userId, eventId) ;

        if(exists) {
            throw new DuplicateNotificationRequestException("Notification request with eventId " + eventId + " already exists.") ;
        }
    }

    private NotificationType validateEventType(String eventType) {
        return ValidatorUtils.validateNotificationTypeOrThrow(eventType) ;
    }

    private UUID validateUser(String userId) {
    	return ValidatorUtils.validateUuidAndGetUuid(userId) ;
    }
}
