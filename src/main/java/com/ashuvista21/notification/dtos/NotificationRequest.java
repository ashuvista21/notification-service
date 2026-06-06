package com.ashuvista21.notification.dtos;

import java.util.Map;

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
    private String eventId ;

    @NotNull
    private String userId ;

    @NotBlank
    private String eventType ;
    
    private String eventContext ;

    private Map<String, String> variables ;
}
