package com.ashuvista21.notification.service;

import java.util.List ;
import java.util.UUID;

import com.ashuvista21.notification.entities.UserChannelContact;
import com.ashuvista21.notification.enums.NotificationChannelType;

public interface UserChannelContactService {
	void updateContact(UUID userId, NotificationChannelType channelType, String contact) ;
	void addContact(UUID userId, NotificationChannelType channelType, String contact) ;
	void disableContact(UUID userId, NotificationChannelType channelType) ;
	void enableContact(UUID userId, NotificationChannelType channelType) ;
	void makePrimaryContact(UUID userId, NotificationChannelType channelType) ;
	UserChannelContact getVerifiedUserChannelContact(UUID userId, NotificationChannelType channelType) ;
	void triggerUserChannelContactVerification(UUID userId, NotificationChannelType channelType) ;
	List<UserChannelContact> getUserChannelContacts(UUID userId) ;
}
