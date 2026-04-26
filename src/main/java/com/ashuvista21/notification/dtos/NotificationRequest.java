package com.ashuvista21.notification.dtos;

import java.util.Map;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest {
    @NotNull
    private UUID eventId ;

    @NotNull
    private UUID userId ;

    @NotBlank
    private String eventType ;

    private Map<String, Object> variables ;
}
