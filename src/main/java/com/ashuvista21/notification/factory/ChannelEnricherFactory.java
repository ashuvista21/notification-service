package com.ashuvista21.notification.factory;

import java.util.List ;
import java.util.Map ;
import java.util.stream.Collectors ;

import org.springframework.stereotype.Component ;

import com.ashuvista21.notification.channel.variable.resolver.ChannelVariableEnricher ;
import com.ashuvista21.notification.channel.variable.resolver.channeltype.DefaultChannelVariableEnricher ;
import com.ashuvista21.notification.enums.NotificationChannelType ;

@Component
public class ChannelEnricherFactory {
	private final Map<NotificationChannelType, ChannelVariableEnricher> enricherMap ;
	private final ChannelVariableEnricher defaultEnricher ;

    public ChannelEnricherFactory(List<ChannelVariableEnricher> enrichers, DefaultChannelVariableEnricher defaultEnricher) {
    	this.defaultEnricher = defaultEnricher ;

    	this.enricherMap = enrichers.stream()
                .filter(e -> e.getChannelType() != null)
                .collect(Collectors.toMap(ChannelVariableEnricher::getChannelType, e -> e)) ;
    }

    public ChannelVariableEnricher get(NotificationChannelType channelType) {
        return enricherMap.getOrDefault(channelType, defaultEnricher) ;
    }
}
