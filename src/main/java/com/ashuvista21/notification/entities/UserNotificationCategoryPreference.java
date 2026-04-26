package com.ashuvista21.notification.entities;

import java.time.Instant ;
import java.util.Set ;
import java.util.UUID ;

import com.ashuvista21.notification.enums.NotificationCategory ;
import com.ashuvista21.notification.enums.NotificationChannelType ;

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
    name = "user_category_preferences",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_user_category",
            columnNames = {"user_id", "category"}
        )
    },
    indexes = {
        @Index(name = "idx_ucp_user_id", columnList = "user_id")
    }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserNotificationCategoryPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @Column(name = "user_id", nullable = false)
    private UUID userId ;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private NotificationCategory category ;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "user_category_channels",
        joinColumns = @JoinColumn(name = "category_pref_id"),
        uniqueConstraints = {
            @UniqueConstraint(
                name = "uk_category_channel",
                columnNames = {"category_pref_id", "channel"}
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
        Instant now = Instant.now();
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