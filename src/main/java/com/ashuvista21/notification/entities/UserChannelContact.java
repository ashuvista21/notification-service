package com.ashuvista21.notification.entities;

import java.time.Instant;
import java.util.UUID;

import com.ashuvista21.notification.enums.NotificationChannelType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "user_channel_contact",
    indexes = {
        @Index(name = "idx_user_channel", columnList = "userId, channel"),
        @Index(name = "idx_value", columnList = "value")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_user_channel_value",
            columnNames = {"userId", "channel", "value"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserChannelContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @Column(nullable = false)
    private UUID userId ;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannelType channel ;

    @Column(nullable = false)
    private String value ; // email or phone or whatsapp or fcmdeviceid or any sender identifier

    private Boolean verified ;

    private Boolean primaryContact ;
    
    private Boolean enabledFlag ;

    @Column(nullable = false, updatable = false)
    private Instant createdAt ;

    @Column(nullable = false)
    private Instant updatedAt ;

    // 🔥 Automatically called before insert
    @PrePersist
    public void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now ;
        this.updatedAt = now ;

        // Optional defaults
        if (this.verified == null) {
            this.verified = false ;
        }
        if (this.primaryContact == null) {
            this.primaryContact = false ;
        }
        if(this.enabledFlag == null) {
        	this.enabledFlag = true ;
        }
    }

    // 🔥 Automatically called before update
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now() ;
    }
}
