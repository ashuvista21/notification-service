package com.ashuvista21.notification.service.impl;

import java.util.Map ;
import java.util.Set ;
import java.util.UUID ;

import org.springframework.stereotype.Service ;

import com.ashuvista21.notification.enums.NotificationCategory ;
import com.ashuvista21.notification.enums.NotificationChannelType ;
import com.ashuvista21.notification.enums.NotificationType ;
import com.ashuvista21.notification.service.ChannelResolver ;
import com.ashuvista21.notification.service.UserNotificationCategoryPreferenceService ;
import com.ashuvista21.notification.service.UserNotificationTypePreferenceService ;

import lombok.RequiredArgsConstructor ;

@Service
@RequiredArgsConstructor
public class ChannelResolverImpl implements ChannelResolver {
	
	private final UserNotificationCategoryPreferenceService notificationCategoryPreferenceService ;
	private final UserNotificationTypePreferenceService notificationTypePreferenceService ;
	
	private static final Map<NotificationCategory, Set<NotificationChannelType>> DEFAULT_CHANNELS = Map.of(
			NotificationCategory.OTP, Set.of(NotificationChannelType.SMS), 
			NotificationCategory.SECURITY, Set.of(NotificationChannelType.EMAIL, NotificationChannelType.SMS), 
			NotificationCategory.TRANSACTIONAL, Set.of(NotificationChannelType.EMAIL), 
			NotificationCategory.INFORMATION, Set.of(NotificationChannelType.EMAIL), 
			NotificationCategory.PROMOTIONAL, Set.of(NotificationChannelType.PUSH)) ;
	
	@Override
	public Set<NotificationChannelType> resolve(UUID userId, NotificationType notificationType) {
		// 1. Check type override
		Set<NotificationChannelType> overrideChannels = notificationTypePreferenceService.getUserChannels(userId, 
				notificationType) ;
		
		if(!overrideChannels.isEmpty()) {
			return overrideChannels ;
		}
		
		// 2. Fallback to category
		NotificationCategory category = notificationType.getCategory() ;
		
		Set<NotificationChannelType> categoryChannels = notificationCategoryPreferenceService.getUserChannels(userId,
						category) ;
		
		if(!categoryChannels.isEmpty()) {
			return categoryChannels ;
		}

		// 3. System default (THIS is defaultChannels)
		return DEFAULT_CHANNELS.getOrDefault(category, Set.of(NotificationChannelType.EMAIL)) ;
	}

}
