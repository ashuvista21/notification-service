package com.ashuvista21.notification.entities;

import java.time.Instant ;
import java.util.UUID;

import com.ashuvista21.notification.enums.NotificationChannelType;
import com.ashuvista21.notification.enums.NotificationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist ;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notification_channels")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationChannelStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification ;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannelType channelType ;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status ;

    private int retryCount ;

    private String providerMessageId ;

    private String errorMessage ;

    private Instant createdAt ;

    private Instant sentAt ;
    
    // 🔥 Automatically called before insert
    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now() ;
    }
}
