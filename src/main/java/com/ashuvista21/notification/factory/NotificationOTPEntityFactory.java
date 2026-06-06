package com.ashuvista21.notification.factory;

import org.springframework.stereotype.Component ;

import com.ashuvista21.notification.entities.NotificationOtp ;
import com.core.otp.dto.OtpGenerateRequest ;
import com.core.otp.enums.OtpPurpose ;
import com.core.otp.factory.OtpEntityFactory ;

@Component
public class NotificationOTPEntityFactory implements OtpEntityFactory<NotificationOtp>{

	@Override
	public NotificationOtp createOtpEntity(String otpHash, OtpPurpose otpPurpose, OtpGenerateRequest generateRequest) {
		return new NotificationOtp(otpHash, otpPurpose, generateRequest.referenceId()) ;
	}

}
