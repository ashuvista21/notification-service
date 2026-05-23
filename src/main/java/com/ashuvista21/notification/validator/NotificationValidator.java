package com.ashuvista21.notification.validator;

import com.ashuvista21.notification.dtos.NotificationInboundEvent ;
import com.ashuvista21.notification.dtos.NotificationRequest;

public interface NotificationValidator {
    void validate(NotificationRequest request) ;
    void validate(NotificationInboundEvent event) ;
}
