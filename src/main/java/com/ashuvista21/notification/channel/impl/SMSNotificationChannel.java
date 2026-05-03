package com.ashuvista21.notification.channel.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient ;

import com.ashuvista21.notification.channel.NotificationChannel;
import com.ashuvista21.notification.config.SMSProperties ;
import com.ashuvista21.notification.dtos.ChannelPayload ;
import com.ashuvista21.notification.enums.NotificationChannelType;

import lombok.AllArgsConstructor ;
import lombok.Builder ;
import lombok.Getter ;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SMSNotificationChannel implements NotificationChannel {
	
	private final WebClient webClient ;
	private final SMSProperties smsProperties ; 
	
	@Override
	public NotificationChannelType getChannelType() {
		return NotificationChannelType.SMS ;
	}

	@Override
	public void send(ChannelPayload channelPayload) {
		SMSRequest request = SMSRequest.builder()
                .to(channelPayload.getRecipientAddress())
                .message(channelPayload.getVariables().getOrDefault("message", channelPayload.getNotificationType())
                		.toString())
                .build() ;

        webClient.post()
                .uri(smsProperties.getUrl())
                .header("Authorization", "Bearer " + smsProperties.getApiKey())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> {
                    // log + retry logic later if needed
                    throw new RuntimeException("SMS sending failed", error) ;
                })
                .block() ; // blocking since your flow seems sync
    }
	
	//👇 Inner class (scoped only to SMS channel)
	@Builder
	@AllArgsConstructor
	@Getter
	private static class SMSRequest {
		private String to ;
		private String message ;
	}
}
