package com.ashuvista21.notification.service.impl;

import java.util.ArrayList ;
import java.util.List ;
import java.util.Set ;
import java.util.UUID ;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional ;

import com.ashuvista21.notification.config.NotificationChannelProperties ;
import com.ashuvista21.notification.dtos.NotificationCommand ;
import com.ashuvista21.notification.dtos.NotificationEvent ;
import com.ashuvista21.notification.dtos.NotificationStatusView ;
import com.ashuvista21.notification.dtos.NotificationStatusView.ChannelStatus ;
import com.ashuvista21.notification.entities.Notification;
import com.ashuvista21.notification.entities.NotificationChannelStatus ;
import com.ashuvista21.notification.enums.NotificationChannelType ;
import com.ashuvista21.notification.enums.NotificationStatus ;
import com.ashuvista21.notification.enums.NotificationType ;
import com.ashuvista21.notification.exceptions.notification.NotificationNotFoundException ;
import com.ashuvista21.notification.repository.NotificationRepository ;
import com.ashuvista21.notification.service.ChannelResolver ;
import com.ashuvista21.notification.service.EventOutboxService ;
import com.ashuvista21.notification.service.NotificationService;
import com.ashuvista21.notification.utils.DateTimeUtils ;
import com.github.f4b6a3.uuid.UuidCreator ;

import lombok.RequiredArgsConstructor ;

@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {

	private final ChannelResolver channelResolver ;
	private final NotificationRepository notificationRepository ;
	private final EventOutboxService eventOutboxService ;
	private final NotificationChannelProperties properties ;
	
	@Transactional
	@Override
    public void createAndDispatch(NotificationCommand command) {

        // 1️⃣ Build Notification entity
        Notification notification = buildNotification(command) ;

        // 2️⃣ Resolve channels
        Set<NotificationChannelType> channels = channelResolver.resolve(notification.getUserId(),
                                        notification.getNotificationType(), command.eventContext()) ;

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
        		properties.getDispatcherTopic(),
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

	@Override
	public List<NotificationStatusView> getNotificationsForUser(UUID userId, int page, int size) {
		return null ;
	}

	@Override
	public NotificationStatusView getNotificationById(UUID userId, UUID notificationId) {
		Notification notification = notificationRepository
				.findById(notificationId)
				.orElseThrow(() -> new NotificationNotFoundException("Notification not found with id : " + notificationId)) ;
		
		NotificationStatusView view = NotificationStatusView.builder()
				.notificationId(notification.getId().toString())
				.overallStatus(notification.getStatus().toString())
				.notificationType(notification.getNotificationType().toString())
				.notificationcategory(notification.getCategory().toString())
				.createdAt(DateTimeUtils.format(notification.getCreatedAt()))
				.channelStatuses(notification.getChannels().stream().map(channelStatus -> 
					ChannelStatus.builder()
							.channelId(channelStatus.getId().toString())
							.channel(channelStatus.getChannelType().toString())
							.status(channelStatus.getStatus().toString())
							.errorMessage(channelStatus.getErrorMessage().isBlank() ? null : channelStatus.getErrorMessage())
							.createdAt(DateTimeUtils.format(channelStatus.getCreatedAt()))
							.sentAt(channelStatus.getSentAt() != null ? DateTimeUtils.format(channelStatus.getSentAt()) : null)
							.build()
				).toList())
				.build() ;
		
		return view ;
		
	}
}
