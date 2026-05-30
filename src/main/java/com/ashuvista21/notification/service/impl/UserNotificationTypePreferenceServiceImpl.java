package com.ashuvista21.notification.service.impl;

import java.util.HashSet;
import java.util.List ;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ashuvista21.notification.dtos.UserPreferenceView ;
import com.ashuvista21.notification.entities.UserNotificationTypePreference ;
import com.ashuvista21.notification.enums.NotificationChannelType;
import com.ashuvista21.notification.enums.NotificationType;
import com.ashuvista21.notification.repository.UserNotificationTypePreferenceRepository ;
import com.ashuvista21.notification.service.UserNotificationTypePreferenceService ;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserNotificationTypePreferenceServiceImpl implements UserNotificationTypePreferenceService {

    private final UserNotificationTypePreferenceRepository notificationTypePreferenceRepository ;

    @Override
    @Transactional
    public void updateUserPreferences(UUID userId, NotificationType notificationType, Set<NotificationChannelType> preferredChannels) {

    	UserNotificationTypePreference userNotificationTypePreference = getOrCreatePreference(userId, notificationType) ;
    	userNotificationTypePreference.setChannels(preferredChannels) ;
    }

    @Override
    @Transactional
    public void addUserPreference(UUID userId, NotificationType notificationType, NotificationChannelType channel) {

    	UserNotificationTypePreference userNotificationTypePreference = getOrCreatePreference(userId, notificationType) ;

        Set<NotificationChannelType> preferredChannels = userNotificationTypePreference.getChannels() ;
        if (preferredChannels == null) {
            preferredChannels = new HashSet<>() ;
        }

        if (!preferredChannels.contains(channel)) {
            preferredChannels.add(channel) ;
        }

        userNotificationTypePreference.setChannels(preferredChannels) ;
    }

    @Override
    @Transactional(readOnly = true)
    public Set<NotificationChannelType> getUserChannels(UUID userId, NotificationType notificationType) {

        return notificationTypePreferenceRepository.findByUserIdAndNotificationType(userId, notificationType)
                .map(UserNotificationTypePreference::getChannels)
                .orElseGet(HashSet::new) ;
    }

    private UserNotificationTypePreference getOrCreatePreference(UUID userId, NotificationType notificationType) {
        return notificationTypePreferenceRepository.findByUserIdAndNotificationType(userId, notificationType)
                .orElseGet(() -> {
                    UserNotificationTypePreference pref = UserNotificationTypePreference.builder()
                            .userId(userId)
                            .notificationType(notificationType)
                            .channels(new HashSet<>())
                            .build() ;

                    return notificationTypePreferenceRepository.save(pref) ;
                }) ;
    }
    
    @Override
	public List<UserPreferenceView> getUserPreferences(UUID userId) {
		List<UserNotificationTypePreference> prefs = notificationTypePreferenceRepository.findByUserId(userId) ;
		
		List<UserPreferenceView> preferenceViews = prefs.stream()
				.map(pref -> new UserPreferenceView(
						null,
						pref.getNotificationType().toString(),
						pref.getChannels().stream()
									.map(NotificationChannelType::toString)
									.toArray(String[]::new)))
				.toList() ;
		
		return preferenceViews ;
	}

}
