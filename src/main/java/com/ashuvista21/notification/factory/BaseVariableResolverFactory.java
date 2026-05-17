package com.ashuvista21.notification.factory;

import java.util.List ;
import java.util.Map ;
import java.util.stream.Collectors ;

import org.springframework.stereotype.Component ;

import com.ashuvista21.notification.channel.variable.resolver.BaseVariableResolver ;
import com.ashuvista21.notification.enums.NotificationCategory ;
import com.ashuvista21.notification.enums.NotificationType ;

@Component
public class BaseVariableResolverFactory {
	private final Map<NotificationCategory, BaseVariableResolver> resolverMap ;

    public BaseVariableResolverFactory(List<BaseVariableResolver> resolvers) {
        resolverMap = resolvers.stream()
                .collect(Collectors.toMap(BaseVariableResolver::getCategory, r -> r)) ;
    }

    public BaseVariableResolver get(NotificationType type) {
        return resolverMap.get(type.getCategory()) ;
    }
}
