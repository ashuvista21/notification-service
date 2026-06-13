package com.ashuvista21.notification.enums;

public enum NotificationType {

    // OTP events
    CHANNEL_VERIFICATION(NotificationCategory.OTP, false, false, OtpCharactersCombination.NUMERIC),
    TWO_FACTOR_AUTH(NotificationCategory.OTP, false, false, OtpCharactersCombination.NUMERIC),
    PASSWORD_RESET_VERIFICATION(NotificationCategory.OTP, false, false, OtpCharactersCombination.ALPHANUMERIC),
    PASSWORD_CHANGE_VERIFICATION(NotificationCategory.OTP, false, false, OtpCharactersCombination.ALPHANUMERIC),
    TRANSACTION_OTP(NotificationCategory.OTP, false, false, OtpCharactersCombination.NUMERIC),

    // INFORMATION events
    CHANNEL_VERIFICATION_SUCCESS(NotificationCategory.INFORMATION, false, false, null),
    PASSWORD_RESET_SUCCESS(NotificationCategory.INFORMATION, false, false, null),
    PROFILE_UPDATED(NotificationCategory.INFORMATION, false, false, null),
    KYC_COMPLETED(NotificationCategory.INFORMATION, false, false, null),
    ACCOUNT_ACTIVATED(NotificationCategory.INFORMATION, false, false, null),
    ACCOUNT_DEACTIVATED(NotificationCategory.INFORMATION, false, false, null),
    GENERAL_ANNOUNCEMENT(NotificationCategory.INFORMATION, false, false, null),
    
    // TRANSACTIONAL events
    PAYMENT_SUCCESS(NotificationCategory.TRANSACTIONAL, false, true, null),
    PAYMENT_FAILED(NotificationCategory.TRANSACTIONAL, false, true, null),
    REFUND_INITIATED(NotificationCategory.TRANSACTIONAL, false, true, null),
    REFUND_COMPLETED(NotificationCategory.TRANSACTIONAL, false, true, null),
    ORDER_PLACED(NotificationCategory.TRANSACTIONAL, false, true, null),
    ORDER_CANCELLED(NotificationCategory.TRANSACTIONAL, false, true, null),
    INVOICE_GENERATED(NotificationCategory.TRANSACTIONAL, false, true, null),
    SUBSCRIPTION_STARTED(NotificationCategory.TRANSACTIONAL, false, true, null),
    SUBSCRIPTION_RENEWED(NotificationCategory.TRANSACTIONAL, false, true, null),
    SUBSCRIPTION_CANCELLED(NotificationCategory.TRANSACTIONAL, false, true, null),
    
    // SECURITY events
    PASSWORD_CHANGED(NotificationCategory.SECURITY, false, false, null),
    LOGIN_FROM_NEW_DEVICE(NotificationCategory.SECURITY, false, false, null),
    SUSPICIOUS_ACTIVITY_DETECTED(NotificationCategory.SECURITY, false, false, null),
    ACCOUNT_LOCKED(NotificationCategory.SECURITY, false, false, null),
    FAILED_LOGIN_ATTEMPTS(NotificationCategory.SECURITY, false, false, null),
    KYC_UPDATED(NotificationCategory.SECURITY, false, false, null),
    SECURITY_SETTINGS_CHANGED(NotificationCategory.SECURITY, false, false, null),
    
    // PROMOTIONAL events
    DISCOUNT_OFFER(NotificationCategory.PROMOTIONAL, false, false, null),
    CASHBACK_OFFER(NotificationCategory.PROMOTIONAL, false, false, null),
    NEW_FEATURE_ANNOUNCEMENT(NotificationCategory.PROMOTIONAL, false, false, null),
    PRODUCT_RECOMMENDATION(NotificationCategory.PROMOTIONAL, false, false, null),
    SEASONAL_SALE(NotificationCategory.PROMOTIONAL, false, false, null),
    REFERRAL_BONUS(NotificationCategory.PROMOTIONAL, false, false, null),
    REMINDER_ABANDONED_CART(NotificationCategory.PROMOTIONAL, false, false, null) ;
	
    private final NotificationCategory category ;
    private final boolean mandatory ;
    private final boolean idempotency ;
    private final OtpCharactersCombination otpCharsType ;

    NotificationType(NotificationCategory category,
    		boolean mandatory,
    		boolean idempotency,
    		OtpCharactersCombination otpCharsType) {
        this.category = category ;
        this.mandatory = mandatory ;
        this.idempotency = idempotency ;
        this.otpCharsType = otpCharsType ;
    }

    public NotificationCategory getCategory() {
        return category ;
    }
    
    public boolean isMandatory() {
    	return mandatory ;
    }
    
    public boolean getIdempotencyFlag() {
    	return idempotency ;
    }
    
    public OtpCharactersCombination getOtpCharsType() {
		return otpCharsType ;
	}
}
