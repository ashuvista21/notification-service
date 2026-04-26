package com.ashuvista21.notification.validator;

import java.util.Set;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ashuvista21.notification.dtos.NotificationRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Component
public class NotificationKafkaConsumer {

    private final Validator validator;

    public NotificationKafkaConsumer(Validator validator) {
        this.validator = validator;
    }

    @KafkaListener(topics = "notification-topic")
    public void consume(NotificationRequest request) {

        Set<ConstraintViolation<NotificationRequest>> violations = validator.validate(request) ;

        if (!violations.isEmpty()) {
            handleInvalidMessage(request, violations) ;
            return ;
        }

        processValidMessage(request) ;
    }
    
    private void handleInvalidMessage(NotificationRequest request, Set<ConstraintViolation<NotificationRequest>> violations) {
    	//log.error("Invalid Kafka message: {}", violations) ;
		//kafkaTemplate.send("notification-dlq", request) ;
    }
    
    private void processValidMessage(NotificationRequest request) {
    	
    }
}
