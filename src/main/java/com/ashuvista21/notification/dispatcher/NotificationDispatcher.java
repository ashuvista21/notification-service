package com.ashuvista21.notification.dispatcher ;

import java.util.UUID ;
import java.util.concurrent.ExecutorService ;

import org.springframework.stereotype.Component ;
import org.springframework.transaction.annotation.Transactional ;

import com.ashuvista21.notification.entities.Notification ;
import com.ashuvista21.notification.entities.NotificationChannelStatus ;
import com.ashuvista21.notification.exceptions.notification.NotificationNotFoundException ;
import com.ashuvista21.notification.repository.NotificationRepository ;
import com.ashuvista21.notification.service.ChannelProcessor ;

import lombok.RequiredArgsConstructor ;

@RequiredArgsConstructor
@Component
public class NotificationDispatcher {

	private final NotificationRepository notificationRepository ;
	private final ExecutorService notificationExecutor ;
	private final ChannelProcessor channelProcessor ;
	
	@Transactional
	public void dispatch(UUID notificationId) {

		Notification notification = getNotificationEntity(notificationId) ;
		
		for (NotificationChannelStatus channelStatus : notification.getChannels()) {
			notificationExecutor.submit(() -> channelProcessor.processChannel(channelStatus.getId(), notificationId)) ;
		}
	}
	
	@Transactional
	public Notification getNotificationEntity(UUID id) {
		return notificationRepository
				.findById(id)
				.orElseThrow(() -> new NotificationNotFoundException("Notification not found")) ;
	}
}
