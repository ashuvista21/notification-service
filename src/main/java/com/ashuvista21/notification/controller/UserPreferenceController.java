package com.ashuvista21.notification.controller;

import java.util.HashSet ;
import java.util.List ;
import java.util.Set ;
import java.util.UUID ;
import java.util.stream.Stream ;

import org.springframework.http.HttpStatus ;
import org.springframework.http.ResponseEntity ;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable ;
import org.springframework.web.bind.annotation.PutMapping ;
import org.springframework.web.bind.annotation.RequestBody ;
import org.springframework.web.bind.annotation.RequestMapping ;
import org.springframework.web.bind.annotation.RestController ;

import com.ashuvista21.notification.dtos.ApiResponse ;
import com.ashuvista21.notification.dtos.UserPreferenceView ;
import com.ashuvista21.notification.enums.NotificationCategory ;
import com.ashuvista21.notification.enums.NotificationChannelType ;
import com.ashuvista21.notification.enums.NotificationType ;
import com.ashuvista21.notification.service.UserNotificationCategoryPreferenceService ;
import com.ashuvista21.notification.service.UserNotificationTypePreferenceService ;
import com.ashuvista21.notification.utils.ValidatorUtils ;

import lombok.RequiredArgsConstructor ;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-preferences")
public class UserPreferenceController {
	
	private final UserNotificationCategoryPreferenceService categoryPreferenceService ;
	private final UserNotificationTypePreferenceService typePreferenceService ;
	
	@PutMapping("/{userId}/category/{categoryName}/update")
	public ResponseEntity<ApiResponse<Void>> updateUserPreferencesByNotificationCategory(
			@PathVariable String userId,
			@PathVariable String categoryName,
			@RequestBody String[] preferredChannels) {
		UUID userUuid = UUID.fromString(userId) ;
		NotificationCategory category = ValidatorUtils.validateCategoryOrThrow(categoryName) ;
		
		Set<NotificationChannelType> channelTypes = new HashSet<>() ;
		
		for (String channel : preferredChannels) {
			channelTypes.add(ValidatorUtils.validateChannelOrThrow(channel)) ;
		}
		
		categoryPreferenceService.updateUserPreferences(userUuid, category, channelTypes) ;
		
		return ResponseEntity.ok(ApiResponse.<Void>builder()
				.success(true)
				.status(HttpStatus.OK)
				.message(List.of("User preferences updated successfully"))
				.data(null)
				.build()) ;
	}
	
	@PutMapping("/{userId}/category/{categoryName}/add")
	public ResponseEntity<ApiResponse<Void>> addUserPreferencesByNotificationCategory(
			@PathVariable String userId,
			@PathVariable String categoryName,
			@RequestBody String preferredChannel) {
		UUID userUuid = UUID.fromString(userId) ;
		NotificationCategory category = ValidatorUtils.validateCategoryOrThrow(categoryName) ;	
		NotificationChannelType channelType = ValidatorUtils.validateChannelOrThrow(preferredChannel) ;
		
		categoryPreferenceService.addUserPreference(userUuid, category, channelType) ;
		
		return ResponseEntity.ok(ApiResponse.<Void>builder()
				.success(true)
				.status(HttpStatus.OK)
				.message(List.of("User preferences added successfully"))
				.data(null)
				.build()) ;
	}
	
	@PutMapping("/{userId}/notification-type/{notificationType}/update")
	public ResponseEntity<ApiResponse<Void>> updateUserPreferencesByNotificationType(
				@PathVariable String userId,
				@PathVariable String notificationType,
				@RequestBody String[] preferredChannels) {
		UUID userUuid = UUID.fromString(userId) ;
		NotificationType type = ValidatorUtils.validateNotificationTypeOrThrow(notificationType) ;
		
		Set<NotificationChannelType> channelTypes = new HashSet<>() ;
		
		for (String channel : preferredChannels) {
			channelTypes.add(ValidatorUtils.validateChannelOrThrow(channel)) ;
		}
		
		typePreferenceService.updateUserPreferences(userUuid, type, channelTypes) ;
		
		return ResponseEntity.ok(ApiResponse.<Void>builder()
				.success(true)
				.status(HttpStatus.OK)
				.message(List.of("User preferences updated successfully"))
				.data(null)
				.build()) ;
	}
	
	@PutMapping("/{userId}/notification-type/{notificationType}/add")
	public ResponseEntity<ApiResponse<Void>> addUserPreferencesByNotificationType(
			@PathVariable String userId,
			@PathVariable String notificationType,
			@RequestBody String preferredChannel) {
		UUID userUuid = UUID.fromString(userId) ;
		NotificationType type = ValidatorUtils.validateNotificationTypeOrThrow(notificationType) ;	
		NotificationChannelType channelType = ValidatorUtils.validateChannelOrThrow(preferredChannel) ;
		
		typePreferenceService.addUserPreference(userUuid, type, channelType) ;
		
		return ResponseEntity.ok(ApiResponse.<Void>builder()
				.success(true)
				.status(HttpStatus.OK)
				.message(List.of("User preferences added successfully"))
				.data(null)
				.build()) ;
		
	}
	
	@GetMapping("{userId}")
	public ResponseEntity<ApiResponse<List<UserPreferenceView>>> getUserPreferences(@PathVariable String userId) {
		 UUID userUuid = UUID.fromString(userId) ;
		 
		 List<UserPreferenceView> list = Stream.concat(
				 categoryPreferenceService.getUserPreferences(userUuid).stream(),
				 typePreferenceService.getUserPreferences(userUuid).stream())
				 .toList() ;
		 
		 return ResponseEntity.ok(ApiResponse.<List<UserPreferenceView>>builder()
				 .success(true)
				 .status(HttpStatus.OK)
				 .message(List.of("User preferences retrieved successfully"))
				 .data(list)
				 .build()) ;
	}
}
