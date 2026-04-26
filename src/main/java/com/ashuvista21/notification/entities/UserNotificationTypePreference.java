package com.ashuvista21.notification.entities;

import java.time.Instant ;
import java.util.Set ;
import java.util.UUID ;

import com.ashuvista21.notification.enums.NotificationChannelType ;
import com.ashuvista21.notification.enums.NotificationType ;

import jakarta.persistence.CollectionTable ;
import jakarta.persistence.Column ;
import jakarta.persistence.ElementCollection ;
import jakarta.persistence.Entity ;
import jakarta.persistence.EnumType ;
import jakarta.persistence.Enumerated ;
import jakarta.persistence.FetchType ;
import jakarta.persistence.GeneratedValue ;
import jakarta.persistence.GenerationType ;
import jakarta.persistence.Id ;
import jakarta.persistence.Index ;
import jakarta.persistence.JoinColumn ;
import jakarta.persistence.PrePersist ;
import jakarta.persistence.PreUpdate ;
import jakarta.persistence.Table ;
import jakarta.persistence.UniqueConstraint ;
import lombok.AllArgsConstructor ;
import lombok.Builder ;
import lombok.Getter ;
import lombok.NoArgsConstructor ;
import lombok.Setter ;

@Entity
@Table(
    name = "user_type_preferences",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_user_notification_type",
            columnNames = {"user_id", "notification_type"}
        )
    },
    indexes = {
        @Index(name = "idx_utp_user_id", columnList = "user_id")
    }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserNotificationTypePreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @Column(name = "user_id", nullable = false)
    private UUID userId ;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType ;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "user_type_channels",
        joinColumns = @JoinColumn(name = "type_pref_id"),
        uniqueConstraints = {
            @UniqueConstraint(
                name = "uk_type_channel",
                columnNames = {"type_pref_id", "channel"}
            )
        }
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    private Set<NotificationChannelType> channels ;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt ;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt ;

    @PrePersist
    public void onCreate() {
        Instant now = Instant.now() ;
        this.createdAt = now ;
        this.updatedAt = now ;

        if (this.channels == null) {
            this.channels = Set.of() ;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now() ;
    }
}