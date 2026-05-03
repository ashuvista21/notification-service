package com.ashuvista21.notification.config;

import java.util.Map ;

import org.springframework.boot.context.properties.ConfigurationProperties ;
import org.springframework.context.annotation.Configuration ;

import com.ashuvista21.notification.enums.NotificationChannelType ;
import com.ashuvista21.notification.enums.ProcessMode ;

import jakarta.annotation.PostConstruct ;
import lombok.Getter ;
import lombok.Setter ;

@Configuration
@ConfigurationProperties(prefix = "notification")
@Getter
@Setter
public class NotificationChannelProperties {
	
	private Map<NotificationChannelType, ChannelConfig> channels ;
	
	// ✅ Default config defined here
    private ChannelConfig defaultConfig ;
	
	@PostConstruct
    public void validate() {
		if (defaultConfig == null) {
            throw new IllegalStateException("Default channel config must be provided") ;
        }
		
        if (channels == null || channels.isEmpty()) {
            throw new IllegalStateException("No channel configurations provided") ;
        }

        channels.forEach((type, config) -> {
            if (config == null) {
                throw new IllegalStateException("ChannelConfig is null for type: " + type);
            }
            config.validate(type);
        });
    }
	
	@Getter
    @Setter
    public static class ChannelConfig {

        private ProcessMode processMode ;
        private String topic ;

        // ✅ Encapsulated validation
        public void validate(NotificationChannelType type) {

            if (processMode == null) {
                throw new IllegalStateException(
                        "ProcessMode must be configured for channel: " + type) ;
            }

            if (processMode == ProcessMode.ASYNC &&
                    (topic == null || topic.isBlank())) {
                throw new IllegalStateException(
                        "Kafka topic must be configured for ASYNC channel: " + type) ;
            }
        }

        // ✅ Helper method (cleaner usage later)
        public boolean isAsync() {
            return processMode == ProcessMode.ASYNC;
        }
    }
}
