package com.ashuvista21.notification.config;

import java.util.HashMap ;
import java.util.Map ;

import org.apache.kafka.clients.producer.ProducerConfig ;
import org.apache.kafka.common.serialization.StringSerializer ;
import org.springframework.context.annotation.Bean ;
import org.springframework.context.annotation.Configuration ;
import org.springframework.kafka.core.DefaultKafkaProducerFactory ;
import org.springframework.kafka.core.KafkaTemplate ;
import org.springframework.kafka.core.ProducerFactory ;

@Configuration
public class KafkaProducerConfig {
	@Bean
    ProducerFactory<String, String> producerFactory(
            KafkaConfigProperties props) {

        Map<String, Object> config = new HashMap<>();

        config.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                props.getBootstrapServers());

        config.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);

        config.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    KafkaTemplate<String, String> kafkaTemplate(
            ProducerFactory<String, String> producerFactory) {

        return new KafkaTemplate<>(producerFactory) ;
    }
}
