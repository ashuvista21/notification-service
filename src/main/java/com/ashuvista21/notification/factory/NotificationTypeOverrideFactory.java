package com.ashuvista21.notification.factory;

import java.util.List ;
import java.util.Map ;
import java.util.Optional ;
import java.util.stream.Collectors ;

import org.springframework.stereotype.Component ;

import com.ashuvista21.notification.channel.variable.resolver.NotificationTypeOverride ;
import com.ashuvista21.notification.enums.NotificationType ;

@Component
public class NotificationTypeOverrideFactory {
	private final Map<NotificationType, NotificationTypeOverride> overrideMap ;

    public NotificationTypeOverrideFactory(List<NotificationTypeOverride> overrides) {
        this.overrideMap = overrides.stream()
                .collect(Collectors.toMap(NotificationTypeOverride::getType, o -> o)) ;
    }

    public Optional<NotificationTypeOverride> get(NotificationType type) {
        return Optional.ofNullable(overrideMap.get(type)) ;
    }
}
