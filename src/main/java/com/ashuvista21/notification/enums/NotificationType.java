package com.ashuvista21.notification.enums;

public enum NotificationType {

    // OTP events
    CHANNEL_VERIFICATION(NotificationCategory.OTP, false, OtpCharactersCombination.NUMERIC),
    TWO_FACTOR_AUTH(NotificationCategory.OTP, false, OtpCharactersCombination.NUMERIC),
    PASSWORD_RESET_VERIFICATION(NotificationCategory.OTP, false, OtpCharactersCombination.ALPHANUMERIC),
    PASSWORD_CHANGE_VERIFICATION(NotificationCategory.OTP, false, OtpCharactersCombination.ALPHANUMERIC),
    TRANSACTION_OTP(NotificationCategory.OTP, false, OtpCharactersCombination.NUMERIC),

    // INFORMATION events
    CHANNEL_VERIFICATION_SUCCESS(NotificationCategory.INFORMATION, false, null),
    PASSWORD_RESET_SUCCESS(NotificationCategory.INFORMATION, false, null),
    PROFILE_UPDATED(NotificationCategory.INFORMATION, false, null),
    KYC_COMPLETED(NotificationCategory.INFORMATION, false, null),
    ACCOUNT_ACTIVATED(NotificationCategory.INFORMATION, false, null),
    ACCOUNT_DEACTIVATED(NotificationCategory.INFORMATION, false, null),
    GENERAL_ANNOUNCEMENT(NotificationCategory.INFORMATION, false, null),
    
    // TRANSACTIONAL events
    PAYMENT_SUCCESS(NotificationCategory.TRANSACTIONAL, false, null),
    PAYMENT_FAILED(NotificationCategory.TRANSACTIONAL, false, null),
    REFUND_INITIATED(NotificationCategory.TRANSACTIONAL, false, null),
    REFUND_COMPLETED(NotificationCategory.TRANSACTIONAL, false, null),
    ORDER_PLACED(NotificationCategory.TRANSACTIONAL, false, null),
    ORDER_CANCELLED(NotificationCategory.TRANSACTIONAL, false, null),
    INVOICE_GENERATED(NotificationCategory.TRANSACTIONAL, false, null),
    SUBSCRIPTION_STARTED(NotificationCategory.TRANSACTIONAL, false, null),
    SUBSCRIPTION_RENEWED(NotificationCategory.TRANSACTIONAL, false, null),
    SUBSCRIPTION_CANCELLED(NotificationCategory.TRANSACTIONAL, false, null),
    
    // SECURITY events
    PASSWORD_CHANGED(NotificationCategory.SECURITY, false, null),
    LOGIN_FROM_NEW_DEVICE(NotificationCategory.SECURITY, false, null),
    SUSPICIOUS_ACTIVITY_DETECTED(NotificationCategory.SECURITY, false, null),
    ACCOUNT_LOCKED(NotificationCategory.SECURITY, false, null),
    FAILED_LOGIN_ATTEMPTS(NotificationCategory.SECURITY, false, null),
    KYC_UPDATED(NotificationCategory.SECURITY, false, null),
    SECURITY_SETTINGS_CHANGED(NotificationCategory.SECURITY, false, null),
    
    // PROMOTIONAL events
    DISCOUNT_OFFER(NotificationCategory.PROMOTIONAL, false, null),
    CASHBACK_OFFER(NotificationCategory.PROMOTIONAL, false, null),
    NEW_FEATURE_ANNOUNCEMENT(NotificationCategory.PROMOTIONAL, false, null),
    PRODUCT_RECOMMENDATION(NotificationCategory.PROMOTIONAL, false, null),
    SEASONAL_SALE(NotificationCategory.PROMOTIONAL, false, null),
    REFERRAL_BONUS(NotificationCategory.PROMOTIONAL, false, null),
    REMINDER_ABANDONED_CART(NotificationCategory.PROMOTIONAL, false, null) ;
	
    private final NotificationCategory category ;
    private final boolean mandatory ;
    private final OtpCharactersCombination otpCharsType ;

    NotificationType(NotificationCategory category,
    		boolean mandatory,
    		OtpCharactersCombination otpCharsType) {
        this.category = category ;
        this.mandatory = mandatory ;
        this.otpCharsType = otpCharsType ;
    }

    public NotificationCategory getCategory() {
        return category ;
    }
    
    public boolean isMandatory() {
    	return mandatory ;
    }
    
    public OtpCharactersCombination getOtpCharsType() {
		return otpCharsType ;
	}
}
