package com.ashuvista21.notification.entities;

import com.core.otp.entities.BaseOtpEntity ;
import com.core.otp.enums.OtpPurpose ;

import jakarta.persistence.Entity ;
import jakarta.persistence.Table ;
import lombok.NoArgsConstructor ;

@Entity
@Table(name = "notification_otp")
@NoArgsConstructor
public class NotificationOtp extends BaseOtpEntity {
	public NotificationOtp(String otpHash, OtpPurpose otpPurpose, String eventRefId) {
		super(otpHash, otpPurpose, eventRefId) ;
	}
}
