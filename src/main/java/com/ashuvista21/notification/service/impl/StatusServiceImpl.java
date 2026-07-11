package com.ashuvista21.notification.service.impl;

import java.util.EnumMap ;
import java.util.List ;
import java.util.Map ;
import java.util.UUID ;

import org.springframework.stereotype.Service ;
import org.springframework.transaction.annotation.Transactional ;

import com.ashuvista21.notification.entities.Notification ;
import com.ashuvista21.notification.entities.NotificationChannelStatus ;
import com.ashuvista21.notification.enums.NotificationStatus ;
import com.ashuvista21.notification.exceptions.notification.NotificationChannelMismatchedException ;
import com.ashuvista21.notification.exceptions.notification.NotificationNotFoundException ;
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
    
    @Transactional
    @Override
    public void markConfigMissing(UUID channelStatusId, Exception ex) {
        NotificationChannelStatus status = getChannelStatus(channelStatusId) ;

        status.setStatus(NotificationStatus.MISSING_CHANNEL_CONFIG) ;
        status.setErrorMessage(ex.getMessage()) ;
        
        channelStatusRepository.save(status) ;
    }

    // ✅ Update overall notification status
    @Transactional
    @Override
    public void updateOverallStatus(UUID notificationId) {
    	Notification notification = getNotificationStatus(notificationId) ;
    	
    	List<NotificationStatus> statuses = notification.getChannels().stream()
				.map(NotificationChannelStatus::getStatus)
				.toList() ;

        NotificationStatus overallStatus = calculateOverallStatus(statuses) ;
        
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
                .orElseThrow(() -> new NotificationChannelMismatchedException("ChannelStatus not found: " + id)) ;
    }
    
    // 🔹 Helper: Fetch notification status safely
    private Notification getNotificationStatus(UUID id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found: " + id)) ;
    }

    // 🔥 Core logic: calculate overall status
    private NotificationStatus calculateOverallStatus(List<NotificationStatus> statuses) {
        
        if(statuses.isEmpty())
        	return NotificationStatus.VOID ;
        
        Map<NotificationStatus, Integer> counts = new EnumMap<>(NotificationStatus.class) ;
        
        for (NotificationStatus status : statuses) {
            counts.put(status, count(counts, status) + 1) ;
        }
        
        int total = statuses.size();

        int processing = count(counts, NotificationStatus.PENDING) ;
        int sent = count(counts, NotificationStatus.SENT) ;
        //int failed = count(counts, NotificationStatus.FAILED);
        //int missingConfig = count(counts, NotificationStatus.MISSING_CHANNEL_CONFIG);
        
        // Still in progress
        if (processing > 0) {
            return NotificationStatus.PENDING ;
        }

        // All successful
        if (sent == total) {
            return NotificationStatus.SENT ;
        }

        // Some successful
        if (sent > 0) {
            return NotificationStatus.PARTIALLY_SENT ;
        }

        // All failed because of missing config
        //if (missingConfig == total) {
        //    return NotificationStatus.MISSING_CHANNEL_CONFIG ;
        //}

        // Remaining terminal state or missing config
        return NotificationStatus.FAILED ;
    }
    
    private static int count(Map<NotificationStatus, Integer> counts, NotificationStatus status) {
    	return counts.getOrDefault(status, 0) ;
    }
}
