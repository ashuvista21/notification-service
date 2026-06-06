package com.ashuvista21.notification.service.impl;

import java.util.List ;
import java.util.UUID ;

import org.springframework.stereotype.Service ;
import org.springframework.transaction.annotation.Transactional ;

import com.ashuvista21.notification.entities.Notification ;
import com.ashuvista21.notification.entities.NotificationChannelStatus ;
import com.ashuvista21.notification.enums.NotificationStatus ;
import com.ashuvista21.notification.repository.NotificationChannelStatusRepository ;
import com.ashuvista21.notification.repository.NotificationRepository ;
import com.ashuvista21.notification.service.StatusService ;

import lombok.RequiredArgsConstructor ;

@Service
@RequiredArgsConstructor
public class StatusServiceImpl implements StatusService {
	private final NotificationChannelStatusRepository channelStatusRepository ;
    private final NotificationRepository notificationRepository ;

    // ✅ Mark channel as SUCCESS
    @Transactional
    @Override
    public void markSuccess(UUID channelStatusId) {

        NotificationChannelStatus status = getChannelStatus(channelStatusId) ;

        status.setStatus(NotificationStatus.SENT);
        status.setErrorMessage(null) ;

        channelStatusRepository.save(status) ;
    }

    // ✅ Mark channel as FAILED
    @Transactional
    @Override
    public void markFailed(UUID channelStatusId, Exception ex) {
        NotificationChannelStatus status = getChannelStatus(channelStatusId) ;

        status.setStatus(NotificationStatus.FAILED) ;
        status.setErrorMessage(ex.getMessage()) ;
        
        channelStatusRepository.save(status) ;
    }

    // ✅ Update overall notification status
    @Transactional
    @Override
    public void updateOverallStatus(UUID notificationId) {
    	Notification notification = getNotificationStatus(notificationId) ;

        NotificationStatus overallStatus = calculateOverallStatus(notification) ;
        
        notification.setStatus(overallStatus) ;

        notificationRepository.save(notification) ;
    }
    
    @Transactional
    @Override
    public void updateProviderMessageId(UUID channelStatusId, String correlationId) {
    	NotificationChannelStatus status = getChannelStatus(channelStatusId) ;
		status.setProviderMessageId(correlationId) ;
    }

    // 🔹 Helper: Fetch channel status safely
    private NotificationChannelStatus getChannelStatus(UUID id) {
        return channelStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ChannelStatus not found: " + id));
    }
    
    // 🔹 Helper: Fetch notification status safely
    private Notification getNotificationStatus(UUID id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + id));
    }

    // 🔥 Core logic: calculate overall status
    private NotificationStatus calculateOverallStatus(Notification notification) {

        List<NotificationChannelStatus> channels = notification.getChannels() ;

        boolean allSuccess = channels.stream()
                .allMatch(c -> c.getStatus() == NotificationStatus.SENT) ;

        boolean anyFailed = channels.stream()
                .anyMatch(c -> c.getStatus() == NotificationStatus.FAILED) ;

        boolean anyInProgress = channels.stream()
                .anyMatch(c -> c.getStatus() == NotificationStatus.PENDING) ;

        if (allSuccess) {
            return NotificationStatus.SENT ;
        }

        if (anyFailed && !anyInProgress) {
            return NotificationStatus.FAILED ;
        }

        return NotificationStatus.PENDING ;
    }
}
