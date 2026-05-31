package com.ashuvista21.notification.controller;

import java.util.List ;

import org.springframework.http.HttpStatus ;
import org.springframework.http.ResponseEntity ;
import org.springframework.web.bind.annotation.GetMapping ;
import org.springframework.web.bind.annotation.PathVariable ;
import org.springframework.web.bind.annotation.RequestMapping ;
import org.springframework.web.bind.annotation.RestController ;

import com.ashuvista21.notification.dtos.ApiResponse ;
import com.ashuvista21.notification.dtos.NotificationDefaultValuesView ;
import com.ashuvista21.notification.enums.NotificationChannelType ;
import com.ashuvista21.notification.enums.NotificationType ;
import com.ashuvista21.notification.service.VariableBuilderService ;
import com.ashuvista21.notification.utils.ValidatorUtils;

import lombok.RequiredArgsConstructor ;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification-configs")
public class NotificationConfigController {
	
	private final VariableBuilderService builderService ;
	
	@GetMapping("/{eventType}/channel/{channel}/variables")
	public ResponseEntity<ApiResponse<NotificationDefaultValuesView>> getConfigVariablesForEventType(
			@PathVariable(name = "eventType") String eventTypeStr,
			@PathVariable(name = "channel") String channelStr) {
		NotificationType eventType = ValidatorUtils.validateNotificationTypeOrThrow(eventTypeStr) ;
		NotificationChannelType channel = ValidatorUtils.validateChannelOrThrow(channelStr) ;
		
		NotificationDefaultValuesView defaultValues = builderService.getDefaultValues(eventType, channel) ;
		
		return ResponseEntity.ok(ApiResponse.<NotificationDefaultValuesView>builder()
				.success(true)
				.status(HttpStatus.OK)
				.message(List.of("Default variable values fetched successfully"))
				.data(defaultValues)
				.build()) ;
	}
}
