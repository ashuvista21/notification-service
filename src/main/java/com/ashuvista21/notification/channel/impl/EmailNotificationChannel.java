package com.ashuvista21.notification.channel.impl;

import java.time.Instant ;
import java.util.Map ;

import org.springframework.stereotype.Service ;
import org.springframework.web.reactive.function.client.WebClient ;

import com.ashuvista21.notification.channel.NotificationChannel;
import com.ashuvista21.notification.config.NotificationChannelProperties ;
import com.ashuvista21.notification.config.NotificationChannelProperties.ChannelConfig ;
import com.ashuvista21.notification.dtos.ChannelPayload ;
import com.ashuvista21.notification.enums.NotificationChannelType;
import com.github.f4b6a3.uuid.UuidCreator ;

import lombok.AllArgsConstructor ;
import lombok.Builder ;
import lombok.Getter ;

@Service
public class EmailNotificationChannel implements NotificationChannel {
	private final WebClient webClient ;
	private final ChannelConfig channelConfig ;
	
	public EmailNotificationChannel(WebClient webClient, NotificationChannelProperties properties) {
		this.webClient = webClient ;
		this.channelConfig = properties.getChannels().getOrDefault(getChannelType(), null) ;
		
		if(this.channelConfig == null)
			throw new IllegalStateException(getChannelType().toString() + " Channel Config not found") ;
    }
	
	@Override
	public NotificationChannelType getChannelType() {
		return NotificationChannelType.EMAIL ;
	}

	@Override
	public void send(ChannelPayload channelPayload) {
		EmailRequest request = EmailRequest.builder()
				.eventId(UuidCreator.getTimeOrdered().toString())
				.eventType(channelPayload.getNotificationCategory())
				.recipient(channelPayload.getRecipientAddress())
				.template(channelPayload.getNotificationType())
				.variables(channelPayload.getVariables())
				.timestamp(Instant.now())
				.build() ;
		
		webClient.post()
				.uri(channelConfig.getUrl())
				.header("Authorization", "Bearer " + channelConfig.getApiKey())
				.bodyValue(request)
				.retrieve()
				.bodyToMono(String.class)
				.doOnError(error -> {
						// log + retry logic later if needed
						throw new RuntimeException("SMS sending failed", error) ;
				})
				.block() ; // blocking since your flow seems sync
		
	}
	
	@Builder
	@AllArgsConstructor
	@Getter
	private static class EmailRequest {
		private String eventId ;
        private String eventType ;
        private String recipient ;
        private String template ;
        private Map<String, Object> variables ;
        private Instant timestamp ;
	}

}
