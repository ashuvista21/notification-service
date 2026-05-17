package com.ashuvista21.notification.service.impl;

import java.util.ArrayList ;
import java.util.Set ;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional ;

import com.ashuvista21.notification.dispatcher.NotificationDispatcher;
import com.ashuvista21.notification.dtos.NotificationEvent ;
import com.ashuvista21.notification.dtos.NotificationRequest;
import com.ashuvista21.notification.entities.Notification;
import com.ashuvista21.notification.entities.NotificationChannelStatus ;
import com.ashuvista21.notification.enums.NotificationChannelType ;
import com.ashuvista21.notification.enums.NotificationStatus ;
import com.ashuvista21.notification.enums.NotificationType ;
import com.ashuvista21.notification.repository.NotificationRepository ;
import com.ashuvista21.notification.service.ChannelResolver ;
import com.ashuvista21.notification.service.NotificationService;
import com.github.f4b6a3.uuid.UuidCreator ;

import lombok.RequiredArgsConstructor ;

@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {
	
	private final NotificationDispatcher dispatcher ;
	private final ChannelResolver channelResolver ;
	
	private final NotificationRepository notificationRepository ;
	
	@Transactional
	@Override
    public void createAndDispatch(NotificationRequest request) {

        // 1️⃣ Build Notification entity
        Notification notification = buildNotification(request) ;

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
        notificationRepository.save(notification) ;

        // 5️⃣ Publish event (IMPORTANT: after save)
        NotificationEvent event = new NotificationEvent("DISPATCH_NOTIFICATION", notification.getId().toString()) ;
        dispatcher.publishNotification(notification.getId().toString(), event) ;
	}
	
	private Notification buildNotification(NotificationRequest request) {
		NotificationType notificationType = NotificationType.valueOf(request.getEventType()) ;
		
		Notification notification = Notification.builder()
				.id(UuidCreator.getTimeOrdered())
				.userId(request.getUserId())
				.notificationType(notificationType)
				.category(notificationType.getCategory())
				.status(NotificationStatus.CREATED)
				.payload(request.getVariables())
				.channels(new ArrayList<>())
				.build() ;
		
		return notification ;
	}
}
