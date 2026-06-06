package com.ashuvista21.notification.controller;

import java.util.List ;
import java.util.UUID ;

import org.springframework.http.HttpStatus ;
import org.springframework.http.ResponseEntity ;
import org.springframework.web.bind.annotation.GetMapping ;
import org.springframework.web.bind.annotation.PathVariable ;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping ;
import org.springframework.web.bind.annotation.RestController;

import com.ashuvista21.notification.dtos.ApiResponse ;
import com.ashuvista21.notification.dtos.NotificationCommand ;
import com.ashuvista21.notification.dtos.NotificationRequest;
import com.ashuvista21.notification.dtos.NotificationStatusView ;
import com.ashuvista21.notification.enums.NotificationType ;
import com.ashuvista21.notification.service.NotificationService;
import com.ashuvista21.notification.utils.ValidatorUtils ;
import com.ashuvista21.notification.validator.NotificationValidator ;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
	
	private final NotificationService notificationService ;
	private final NotificationValidator notificationValidator ;
	
	@PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendNotification(@RequestBody NotificationRequest notificationRequest) {
		
		notificationValidator.validate(notificationRequest) ;
		
		NotificationCommand command = new NotificationCommand(
				notificationRequest.getEventId(),
				UUID.fromString(notificationRequest.getUserId()), 
				NotificationType.valueOf(notificationRequest.getEventType()),
				"RESt_API",
				notificationRequest.getVariables()) ;
		
        notificationService.createAndDispatch(command) ;
        
        return ResponseEntity.status(HttpStatus.ACCEPTED)
        			.body(ApiResponse.<Void>builder()
        					.status(HttpStatus.ACCEPTED)
        					.success(true)
        					.message(List.of("Notification request accepted for processing"))
        					.data(null)
        					.build()) ;
    }
	
	@GetMapping("/{userId}/status/{notificationId}")
	public ResponseEntity<ApiResponse<NotificationStatusView>> getNotificationStatus(@PathVariable String userId ,@PathVariable String notificationId) {
		
		NotificationStatusView view = notificationService.getNotificationById(
				ValidatorUtils.validateUuidAndGetUuid(userId),
				ValidatorUtils.validateUuidAndGetUuid(notificationId)) ;
		
		return ResponseEntity.ok(
				ApiResponse.<NotificationStatusView>builder()
					.message(List.of("Status fetched successfully"))
					.success(true)
					.status(HttpStatus.OK)
					.data(view)
					.build()
		) ;
	}
}
