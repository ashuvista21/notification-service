package com.ashuvista21.notification.entities;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sms_gateway_devices")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FCMDeviceInfo {

    @Id
    private UUID id ;

    private String deviceName ;

    @Column(nullable = false, unique = true)
    private String fcmToken ;

    private boolean active ;

    private Instant createdAt ;
}
