package com.ashuvista21.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties ;
import org.springframework.context.annotation.Configuration ;

import lombok.Getter ;
import lombok.Setter ;

@Configuration
@ConfigurationProperties(prefix = "notification.sms")
@Getter
@Setter
public class SMSProperties {
    private String url ;
    private String apiKey ;
}
