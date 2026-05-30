package com.ashuvista21.notification.entities;

import com.core.otp.entities.BaseOtpEntity ;
import com.core.otp.enums.OtpPurpose ;

import jakarta.persistence.Entity ;
import jakarta.persistence.Table ;

@Entity
@Table(name = "notification_otp")
public class NotificationOtp extends BaseOtpEntity {
	public NotificationOtp(String otpHash, OtpPurpose otpPurpose, String eventRefId) {
		super(otpHash, otpPurpose, eventRefId) ;
	}
}
