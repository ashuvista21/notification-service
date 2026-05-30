package com.ashuvista21.notification.dtos;

import java.util.Map ;

public record NotificationDefaultValuesView(
		Map<String, String> baseVariables,
        Map<String, String> overrideVariables,
        Map<String, String> enricherVariables) {
}
