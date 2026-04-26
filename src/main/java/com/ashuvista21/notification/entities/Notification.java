package com.ashuvista21.notification.entities;

import java.time.Instant ;
import java.util.List;
import java.util.UUID;

import com.ashuvista21.notification.enums.NotificationCategory;
import com.ashuvista21.notification.enums.NotificationStatus;
import com.ashuvista21.notification.enums.NotificationType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist ;
import jakarta.persistence.PreUpdate ;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id ;

    @Column(nullable = false)
    private UUID userId ;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType ;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationCategory category ;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status ;

    @Column(columnDefinition = "TEXT")
    private String payload ;   // JSON payload containing template data

    private Instant createdAt ;

    private Instant updatedAt ;
    
    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<NotificationChannelStatus> channels ;
    
    // 🔥 Automatically called before insert
    @PrePersist
    public void onCreate() {
        Instant now = Instant.now() ;
        this.createdAt = now ;
        this.updatedAt = now ;
    }

    // 🔥 Automatically called before update
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now() ;
    }
}
