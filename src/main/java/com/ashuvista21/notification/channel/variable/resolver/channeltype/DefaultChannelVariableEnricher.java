package com.ashuvista21.notification.channel.variable.resolver.channeltype;

import java.util.Map ;

import com.ashuvista21.notification.channel.variable.resolver.ChannelVariableEnricher ;
import com.ashuvista21.notification.entities.Notification ;
import com.ashuvista21.notification.enums.NotificationChannelType ;

public class DefaultChannelVariableEnricher implements ChannelVariableEnricher {
	@Override
    public NotificationChannelType getChannelType() {
        return null ; // not used
    }

    @Override
    public Map<String, Object> enrich(Map<String, Object> variables, Notification notification) {
        return variables ;
    }
}
