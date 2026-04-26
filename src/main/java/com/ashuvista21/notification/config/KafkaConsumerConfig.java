package com.ashuvista21.notification.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import com.ashuvista21.notification.dtos.EmailEvent;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {
	
	private final KafkaConfigProperties kafkaConfigProperties ;
	@Value("${kafka.bootstrap-servers}")
    private String bootstrapServers ;

    @Value("${kafka.consumer.group-id:email-service-group}")
    private String groupId ;
    
    @Value("${kafka.topic.partitions:1}")
    private int partitions ;

    @Bean
    ConsumerFactory<String, EmailEvent> consumerFactory() {
    	Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigProperties.getBootstrapServers()) ;
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfigProperties.getConsumer().getGroupId()) ;
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest") ;
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false) ;

        // Use ErrorHandlingDeserializer as the outer deserializer
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        // Tell ErrorHandlingDeserializer which actual deserializers to use under the hood
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        // Tell JsonDeserializer what type to deserialize to
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.ashuvista21.notification.dtos.EmailEvent");
        // or use: props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.emailservice.model");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.ashuvista21.notification.dtos");

        return new DefaultKafkaConsumerFactory<>(props);
    }
    
    @Bean
    ConcurrentKafkaListenerContainerFactory<String, EmailEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, EmailEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        // concurrency: number of threads (set to number of partitions or N)
        factory.setConcurrency(kafkaConfigProperties.getTopics().get("email").getPartitions());

        // Enable manual ack mode if desired:
        factory.getContainerProperties().setAckMode(org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL);

        // Error handler with retries then failure (example: 3 retries, then handle)
        // BackOff: 1000ms between attempts, 3 attempts -> total tries: 1 + 3
        FixedBackOff backOff = new FixedBackOff(1000L, 3L);
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(backOff);
        // Optionally configure what exceptions to not retry or to seek to current after failure:
        // errorHandler.addNotRetryableExceptions(DeserializationException.class);

        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }
}
