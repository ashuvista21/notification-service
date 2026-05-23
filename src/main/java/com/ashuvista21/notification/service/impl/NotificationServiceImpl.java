package com.ashuvista21.notification.service.impl;

import java.util.ArrayList ;
import java.util.Set ;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional ;

import com.ashuvista21.notification.config.NotificationChannelProperties ;
import com.ashuvista21.notification.dtos.NotificationCommand ;
import com.ashuvista21.notification.dtos.NotificationEvent ;
import com.ashuvista21.notification.entities.Notification;
import com.ashuvista21.notification.entities.NotificationChannelStatus ;
import com.ashuvista21.notification.enums.NotificationChannelType ;
import com.ashuvista21.notification.enums.NotificationStatus ;
import com.ashuvista21.notification.enums.NotificationType ;
import com.ashuvista21.notification.repository.NotificationRepository ;
import com.ashuvista21.notification.service.ChannelResolver ;
import com.ashuvista21.notification.service.EventOutboxService ;
import com.ashuvista21.notification.service.NotificationService;
import com.github.f4b6a3.uuid.UuidCreator ;

import lombok.RequiredArgsConstructor ;

@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {

	private final ChannelResolver channelResolver ;
	private final NotificationRepository notificationRepository ;
	private final EventOutboxService eventOutboxService ;
	
	@Transactional
	@Override
    public void createAndDispatch(NotificationCommand command) {

        // 1️⃣ Build Notification entity
        Notification notification = buildNotification(command) ;

        // 2️⃣ Resolve channels
        Set<NotificationChannelType> channels = channelResolver.resolve(notification.getUserId(),
                                        notification.getNotificationType()) ;

        // 3️⃣ Create channel statuses
        for(NotificationChannelType channel : channels) {
            NotificationChannelStatus status = NotificationChannelStatus.builder()
                            .notification(notification)
                            .channelType(channel)
                            .status(NotificationStatus.CREATED)
                            .retryCount(0)
                            .build() ;
            notification.getChannels().add(status) ;
        }

        // 4️⃣ Persist
        Notification notificationEntity = notificationRepository.save(notification) ;
        String notificationId = notificationEntity.getId().toString() ;

        // 5️⃣ Publish event (IMPORTANT: after save)
        NotificationEvent event = new NotificationEvent("NOTIFICATION", notificationId) ;
        eventOutboxService.createEvent(
        		event.originator(),
        		notificationId,
        		new NotificationChannelProperties().getDispatcherTopic(),
        		event) ;
	}
	
	private Notification buildNotification(NotificationCommand command) {
		NotificationType notificationType = command.notificationType() ;
		
		Notification notification = Notification.builder()
				.id(UuidCreator.getTimeOrdered())
				.userId(command.userId())
				.notificationType(notificationType)
				.category(notificationType.getCategory())
				.status(NotificationStatus.CREATED)
				.payload(command.variables())
				.userEventRef(command.userEventRef())
				.channels(new ArrayList<>())
				.build() ;
		
		return notification ;
	}
}
