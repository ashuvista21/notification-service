package com.ashuvista21.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan ;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories ;
import org.springframework.scheduling.annotation.EnableScheduling ;

@SpringBootApplication(
	    scanBasePackages = {
	        "com.ashuvista21.notification",
	        "com.core.otp"
	    }
	)
@EnableJpaRepositories({
	    "com.ashuvista21.notification.repository",
	    "com.core.otp.repository"
})
@EntityScan({
	    "com.ashuvista21.notification.entities",
	    "com.core.otp.entities"
})
@EnableScheduling
public class NotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

}
