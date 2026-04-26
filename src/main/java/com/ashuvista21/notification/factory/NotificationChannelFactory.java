package com.ashuvista21.notification.factory;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ashuvista21.notification.channel.NotificationChannel;
import com.ashuvista21.notification.enums.NotificationChannelType;

@Component
public class NotificationChannelFactory {
	private Map<NotificationChannelType, NotificationChannel> channels ;
	
	public NotificationChannelFactory(List<NotificationChannel> channelsList) {
		this.channels = channelsList.stream()
				.collect(Collectors.toMap(
						NotificationChannel::getChannelType, 
						Function.identity()
				)) ;
	}
	
	public NotificationChannel getChannel(NotificationChannelType type) {
        return channels.getOrDefault(type, null) ;
    }
}
