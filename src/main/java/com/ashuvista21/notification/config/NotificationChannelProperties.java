package com.ashuvista21.notification.config;

import java.util.Map ;

import org.springframework.boot.context.properties.ConfigurationProperties ;
import org.springframework.context.annotation.Configuration ;

import com.ashuvista21.notification.enums.NotificationChannelType ;
import com.ashuvista21.notification.enums.ProcessMode ;

import lombok.Getter ;
import lombok.Setter ;

@Configuration
@ConfigurationProperties(prefix = "notification")
@Getter
@Setter
public class NotificationChannelProperties {
	
	private Map<NotificationChannelType, ChannelConfig> channels ;

    @Getter
    @Setter
    public static class ChannelConfig {
        private ProcessMode processMode ;
        private String topic ;
    }
}
