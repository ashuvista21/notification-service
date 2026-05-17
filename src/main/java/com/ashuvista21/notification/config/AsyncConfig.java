package com.ashuvista21.notification.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AsyncConfig {
	
	@Bean("notificationExecutor")
    Executor emailExecutor() {
        // tune corePoolSize/maximumPoolSize/queue based on load — example uses fixed pool
        return Executors.newFixedThreadPool(10) ;
    }
}
