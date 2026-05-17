package com.ashuvista21.notification.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "kafka")
public class KafkaConfigProperties {
	private String bootstrapServers ;
	
    private Consumer consumer = new Consumer() ;

    @Getter
    @Setter
    public static class Consumer {
        private String groupId ;
        private int concurrency = 1 ; //default
    }
    
    // 🔥 Dynamic topics
    private Map<String, TopicConfig> topics ;

    @Getter
    @Setter
    public static class TopicConfig {
        private String name ;
        private Integer partitions ;
        private Integer replicationFactor = 1 ;
    }
}
