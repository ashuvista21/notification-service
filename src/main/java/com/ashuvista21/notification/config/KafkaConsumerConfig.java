package com.ashuvista21.notification.config;

import java.util.HashMap ;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig ;
import org.apache.kafka.common.serialization.StringDeserializer ;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory ;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory ;
import org.springframework.kafka.listener.ContainerProperties;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

	private final KafkaConfigProperties kafkaConfigProperties;

    @Bean
    ConsumerFactory<String, String> consumerFactory() {

        Map<String, Object> props = new HashMap<>();

        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaConfigProperties.getBootstrapServers());

        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                kafkaConfigProperties.getConsumer().getGroupId());

        props.put(
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "earliest");

        props.put(
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                false);

        props.put(
                ConsumerConfig.MAX_POLL_RECORDS_CONFIG,
                50);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new StringDeserializer());
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, String>
    kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, String>
                factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());

        factory.setConcurrency(
                kafkaConfigProperties
                        .getConsumer()
                        .getConcurrency());

        factory.getContainerProperties()
                .setAckMode(
                        ContainerProperties.AckMode.MANUAL);

        return factory;
    }
}