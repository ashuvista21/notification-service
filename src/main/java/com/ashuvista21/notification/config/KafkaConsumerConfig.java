package com.ashuvista21.notification.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory ;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory ;
import org.springframework.kafka.core.KafkaTemplate ;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import com.ashuvista21.notification.dtos.NotificationEvent;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaConfigProperties kafkaConfigProperties ;

    /**
     * Consumer Factory
     */
    @Bean
    ConsumerFactory<String, NotificationEvent> consumerFactory() {

        Map<String, Object> props = new HashMap<>() ;

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigProperties.getBootstrapServers()) ;
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfigProperties.getConsumer().getGroupId()) ;
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest") ;
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false) ;

        // Throughput tuning
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 50) ;

        // Deserializers
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class) ;
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class) ;

        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class) ;
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class) ;

        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, NotificationEvent.class) ;
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.ashuvista21.notification.dtos") ;

        return new DefaultKafkaConsumerFactory<>(props) ;
    }

    /**
     * Kafka Listener Container Factory
     */
    @Bean
    ConcurrentKafkaListenerContainerFactory<String, NotificationEvent>
    kafkaListenerContainerFactory(KafkaTemplate<String, Object> kafkaTemplate) {

        ConcurrentKafkaListenerContainerFactory<String, NotificationEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>() ;

        factory.setConsumerFactory(consumerFactory()) ;

        // Dynamic concurrency (NOT tied to one topic)
        factory.setConcurrency(kafkaConfigProperties.getConsumer().getConcurrency()) ;

        // Manual acknowledgment
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL) ;

        /**
         * Dead Letter Queue (DLQ) Configuration
         */
        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        kafkaTemplate,
                        (record, ex) -> new TopicPartition(
                                record.topic() + ".DLT", // DLT naming convention
                                record.partition()
                        )
                ) ;

        /**
         * Retry Configuration
         * 3 retries with 1 second delay
         */
        FixedBackOff backOff = new FixedBackOff(1000L, 3L) ;

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff) ;

        // Optional: don't retry deserialization issues
        // errorHandler.addNotRetryableExceptions(DeserializationException.class);

        factory.setCommonErrorHandler(errorHandler) ;

        return factory ;
    }
    
    @Bean
    DefaultErrorHandler errorHandler(
            KafkaTemplate<Object, Object> template) {

        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(template);

        FixedBackOff backOff =
                new FixedBackOff(3000L, 3);

        return new DefaultErrorHandler(
                recoverer,
                backOff
        );
    }
}