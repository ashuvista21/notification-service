package com.ashuvista21.notification.enums;

public enum NotificationType {

    // OTP events
    CHANNEL_VERIFICATION(NotificationCategory.OTP, false),
    TWO_FACTOR_AUTH(NotificationCategory.OTP, false),
    PASSWORD_RESET_VERIFICATION(NotificationCategory.OTP, false),
    PASSWORD_CHANGE_VERIFICATION(NotificationCategory.OTP, false),
    TRANSACTION_OTP(NotificationCategory.OTP, false),

    // INFORMATION events
    EMAIL_VERIFICATION_SUCCESS(NotificationCategory.INFORMATION, false),
    PASSWORD_RESET_SUCCESS(NotificationCategory.INFORMATION, false),
    PROFILE_UPDATED(NotificationCategory.INFORMATION, false),
    KYC_COMPLETED(NotificationCategory.INFORMATION, false),
    ACCOUNT_ACTIVATED(NotificationCategory.INFORMATION, false),
    ACCOUNT_DEACTIVATED(NotificationCategory.INFORMATION, false),
    GENERAL_ANNOUNCEMENT(NotificationCategory.INFORMATION, false),
    
    // TRANSACTIONAL events
    PAYMENT_SUCCESS(NotificationCategory.TRANSACTIONAL, false),
    PAYMENT_FAILED(NotificationCategory.TRANSACTIONAL, false),
    REFUND_INITIATED(NotificationCategory.TRANSACTIONAL, false),
    REFUND_COMPLETED(NotificationCategory.TRANSACTIONAL, false),
    ORDER_PLACED(NotificationCategory.TRANSACTIONAL, false),
    ORDER_CANCELLED(NotificationCategory.TRANSACTIONAL, false),
    INVOICE_GENERATED(NotificationCategory.TRANSACTIONAL, false),
    SUBSCRIPTION_STARTED(NotificationCategory.TRANSACTIONAL, false),
    SUBSCRIPTION_RENEWED(NotificationCategory.TRANSACTIONAL, false),
    SUBSCRIPTION_CANCELLED(NotificationCategory.TRANSACTIONAL, false),
    
    // SECURITY events
    PASSWORD_CHANGED(NotificationCategory.SECURITY, false),
    LOGIN_FROM_NEW_DEVICE(NotificationCategory.SECURITY, false),
    SUSPICIOUS_ACTIVITY_DETECTED(NotificationCategory.SECURITY, false),
    ACCOUNT_LOCKED(NotificationCategory.SECURITY, false),
    FAILED_LOGIN_ATTEMPTS(NotificationCategory.SECURITY, false),
    KYC_UPDATED(NotificationCategory.SECURITY, false),
    SECURITY_SETTINGS_CHANGED(NotificationCategory.SECURITY, false),
    
    // PROMOTIONAL events
    DISCOUNT_OFFER(NotificationCategory.PROMOTIONAL, false),
    CASHBACK_OFFER(NotificationCategory.PROMOTIONAL, false),
    NEW_FEATURE_ANNOUNCEMENT(NotificationCategory.PROMOTIONAL, false),
    PRODUCT_RECOMMENDATION(NotificationCategory.PROMOTIONAL, false),
    SEASONAL_SALE(NotificationCategory.PROMOTIONAL, false),
    REFERRAL_BONUS(NotificationCategory.PROMOTIONAL, false),
    REMINDER_ABANDONED_CART(NotificationCategory.PROMOTIONAL, false) ;

    private final NotificationCategory category ;
    private final boolean mandatory ;

    NotificationType(NotificationCategory category, boolean mandatory) {
        this.category = category ;
        this.mandatory = mandatory ;
    }

    public NotificationCategory getCategory() {
        return category ;
    }
    
    public boolean isMandatory() {
    	return mandatory ;
    }
}
