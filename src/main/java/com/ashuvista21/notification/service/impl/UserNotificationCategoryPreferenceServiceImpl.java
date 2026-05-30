package com.ashuvista21.notification.service.impl;

import java.util.HashSet;
import java.util.List ;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ashuvista21.notification.dtos.UserPreferenceView ;
import com.ashuvista21.notification.entities.UserNotificationCategoryPreference ;
import com.ashuvista21.notification.enums.NotificationCategory ;
import com.ashuvista21.notification.enums.NotificationChannelType;
import com.ashuvista21.notification.repository.UserNotificationCategoryPreferenceRepository ;
import com.ashuvista21.notification.service.UserNotificationCategoryPreferenceService ;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserNotificationCategoryPreferenceServiceImpl implements UserNotificationCategoryPreferenceService {

    private final UserNotificationCategoryPreferenceRepository categoryPreferenceRepository ;

    @Override
    @Transactional
    public void updateUserPreferences(UUID userId, NotificationCategory notificationCategory, Set<NotificationChannelType> preferredChannels) {

        UserNotificationCategoryPreference userNotificationCategoryPreference = getOrCreatePreference(userId, notificationCategory) ;
        userNotificationCategoryPreference.setChannels(preferredChannels) ;
    }

    @Override
    @Transactional
    public void addUserPreference(UUID userId, NotificationCategory notificationCategory, NotificationChannelType channel) {

    	UserNotificationCategoryPreference userNotificationCategoryPreference = getOrCreatePreference(userId, notificationCategory) ;

        Set<NotificationChannelType> preferredChannels = userNotificationCategoryPreference.getChannels() ;
        if (preferredChannels == null) {
            preferredChannels = new HashSet<>() ;
        }

        if (!preferredChannels.contains(channel)) {
            preferredChannels.add(channel) ;
        }

        userNotificationCategoryPreference.setChannels(preferredChannels) ;
    }

    @Override
    @Transactional(readOnly = true)
    public Set<NotificationChannelType> getUserChannels(UUID userId, NotificationCategory notificationCategory) {
        return categoryPreferenceRepository.findByUserIdAndNotificationType(userId, notificationCategory)
                .map(UserNotificationCategoryPreference::getChannels)
                .orElseGet(HashSet::new) ;
    }

    private UserNotificationCategoryPreference getOrCreatePreference(UUID userId, NotificationCategory notificationCategory) {
        return categoryPreferenceRepository.findByUserIdAndNotificationType(userId, notificationCategory)
                .orElseGet(() -> {
                	UserNotificationCategoryPreference pref = UserNotificationCategoryPreference.builder()
                            .userId(userId)
                            .category(notificationCategory)
                            .channels(new HashSet<>())
                            .build() ;

                    return categoryPreferenceRepository.save(pref) ;
                }) ;
    }

	@Override
	public List<UserPreferenceView> getUserPreferences(UUID userId) {
		List<UserNotificationCategoryPreference> prefs = categoryPreferenceRepository.findByUserId(userId) ;
		
		List<UserPreferenceView> preferenceViews = prefs.stream()
				.map(pref -> new UserPreferenceView(
						pref.getCategory().toString(),
						null,
						pref.getChannels().stream()
									.map(NotificationChannelType::toString)
									.toArray(String[]::new)))
				.toList() ;
		
		return preferenceViews ;
	}

}
