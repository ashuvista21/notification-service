package com.ashuvista21.notification.entities;

import java.time.Instant;
import java.util.UUID;

import com.ashuvista21.notification.enums.EventStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "event_outbox",
indexes = {
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_created_at", columnList = "createdAt")
})
public class EventOutbox {

    @Id
    private UUID eventId ;

    private String aggregateType ;   // e.g. USER_CHANNEL_CONTACT
    private UUID aggregateId ;       // userId
    private UUID correlationId ;

    private String eventType ;       // CONTACT_ADDED, CONTACT_VERIFIED

    @Column(columnDefinition = "TEXT")
    private Object payload ;         // JSON

    private String status ;          // PENDING, SENT, FAILED
    
    private int retryCount ;

    private Instant createdAt ;
    private Instant processedAt ;
    private Instant updatedAt ;
    
 // 🔹 Called before insert
    @PrePersist
    public void prePersist() {
        Instant now = Instant.now() ;

        if (eventId == null) {
        	eventId = UUID.randomUUID() ; // ensure eventId always exists
        }

        this.createdAt = now ;
        this.updatedAt = now ;

        if (this.status == null) {
            this.status = EventStatus.PENDING.toString() ;
        }

        this.retryCount = 0 ;
    }

    // 🔹 Called before update
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now() ;
    }
}
