package com.ashuvista21.notification.controller;

import java.util.List ;
import java.util.UUID ;

import org.springframework.http.HttpStatus ;
import org.springframework.http.ResponseEntity ;
import org.springframework.web.bind.annotation.GetMapping ;
import org.springframework.web.bind.annotation.PatchMapping ;
import org.springframework.web.bind.annotation.PathVariable ;
import org.springframework.web.bind.annotation.PostMapping ;
import org.springframework.web.bind.annotation.PutMapping ;
import org.springframework.web.bind.annotation.RequestBody ;
import org.springframework.web.bind.annotation.RequestMapping ;
import org.springframework.web.bind.annotation.RestController ;

import com.ashuvista21.notification.dtos.ApiResponse ;
import com.ashuvista21.notification.dtos.UserChannelContactView ;
import com.ashuvista21.notification.dtos.UserContactRequest ;
import com.ashuvista21.notification.dtos.VerifyOtpRequest ;
import com.ashuvista21.notification.entities.UserChannelContact ;
import com.ashuvista21.notification.enums.NotificationChannelType ;
import com.ashuvista21.notification.service.UserChannelContactService ;
import com.ashuvista21.notification.utils.ValidatorUtils ;

import lombok.RequiredArgsConstructor ;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-contacts")
public class UserContactController {
	
	private final UserChannelContactService channelContactService ;
	
	@PutMapping("/{userId}/update")
	public ResponseEntity<ApiResponse<Void>> updateUserContacts(
			@PathVariable String userId,
			@RequestBody UserContactRequest request) {
		UUID userUuid = ValidatorUtils.validateUuidAndGetUuid(userId) ;
		NotificationChannelType channel = ValidatorUtils.validateChannelOrThrow(request.channel()) ;
		
		if(channel == NotificationChannelType.EMAIL)
			ValidatorUtils.validateEmailFormat(request.value()) ;
		if(channel == NotificationChannelType.SMS)
			ValidatorUtils.validatePhoneNumberFormat(request.value()) ;
		
		channelContactService.updateContact(
				userUuid,
				channel,
				request.value(),
				request.createIfAbsent()) ;
		
		return ResponseEntity.ok(ApiResponse.<Void>builder()
					.status(HttpStatus.OK)
					.message(List.of("User contact updated successfully for channel " + channel))
					.success(true)
					.data(null)
					.build()) ;
	}
	
	@PutMapping("/{userId}/add")
	public ResponseEntity<ApiResponse<Void>> addUserContacts(
			@PathVariable String userId,
			@RequestBody UserContactRequest request) {
		UUID userUuid = ValidatorUtils.validateUuidAndGetUuid(userId) ;
		NotificationChannelType channel = ValidatorUtils.validateChannelOrThrow(request.channel()) ;
		
		if(channel == NotificationChannelType.EMAIL)
			ValidatorUtils.validateEmailFormat(request.value()) ;
		if(channel == NotificationChannelType.SMS)
			ValidatorUtils.validatePhoneNumberFormat(request.value()) ;
		
		channelContactService.addContact(
				userUuid,
				channel,
				request.value(),
				request.overrideIfPresent()) ;
		
		return ResponseEntity.ok(ApiResponse.<Void>builder()
					.status(HttpStatus.OK)
					.message(List.of("User contact updated successfully for channel " + channel))
					.success(true)
					.data(null)
					.build()) ;
	}
	
	@GetMapping("/{userId}")
	public ResponseEntity<ApiResponse<List<UserChannelContactView>>> getUserContacts(
			@PathVariable String userId) {
		UUID userUuid = ValidatorUtils.validateUuidAndGetUuid(userId) ;
		
		List<UserChannelContact> channelContacts = channelContactService.getUserChannelContacts(userUuid) ;
		
		List<UserChannelContactView> views = channelContacts.stream()
				.map(contact -> new UserChannelContactView(
					contact.getChannel().toString(),
					contact.getValue(),
					contact.getVerified(),
					contact.getEnabledFlag()))
				.toList() ;
		
		return ResponseEntity.ok(ApiResponse.<List<UserChannelContactView>>builder()
				.success(true)
				.status(HttpStatus.OK)
				.message(List.of("User Contacts Fetched Succesfully"))
				.data(views)
				.build()) ;
	}
	
	@PostMapping("/{userId}/trigger-verification/{channel}")
	public ResponseEntity<ApiResponse<String>> triggerVerificationForContact(
			@PathVariable(name = "userId") String userIdStr,
			@PathVariable(name = "channel") String channelStr) {
		UUID userId = ValidatorUtils.validateUuidAndGetUuid(userIdStr) ;
		NotificationChannelType channel = ValidatorUtils.validateChannelOrThrow(channelStr) ;
		
		String eventId = channelContactService.triggerUserChannelContactVerification(userId, channel) ;
		
		return ResponseEntity.accepted()
					.body(ApiResponse.<String>builder()
							.status(HttpStatus.ACCEPTED)
							.success(false)
							.message(List.of("User contact verification triggered for channel " + channelStr + ". Please check your " + channel.toString().toLowerCase() + " for the verification code."))
							.data(eventId)
							.build()) ;
	}
	
	@PatchMapping("/{userId}/verify/{channel}")
	public ResponseEntity<ApiResponse<Void>> verifyUserContact(
			@PathVariable(name = "userId") String userIdStr,
			@PathVariable(name = "channel") String channelStr,
			@RequestBody VerifyOtpRequest request) {
		UUID userId = ValidatorUtils.validateUuidAndGetUuid(userIdStr) ;
		NotificationChannelType channel = ValidatorUtils.validateChannelOrThrow(channelStr) ;
		UUID requestId = ValidatorUtils.validateUuidAndGetUuid(request.requestId()) ;
		
		channelContactService.verifyUserChannelContact(requestId, userId, channel, request.otp()) ;
		
		return ResponseEntity.ok(ApiResponse.<Void>builder()
				.status(HttpStatus.OK)
				.success(true)
				.message(List.of("User contact verified successfully for channel " + channel))
				.data(null)
				.build()) ;
	}
	
	@PatchMapping("/{userId}/enable/{channel}")
	public ResponseEntity<ApiResponse<Void>> enableUserContact(UUID userId, NotificationChannelType channel) {
		channelContactService.enableContact(userId, channel) ;
		
		return ResponseEntity.ok(ApiResponse.<Void>builder()
				.status(HttpStatus.OK)
				.success(true)
				.message(List.of("User contact enabled successfully for channel " + channel))
				.data(null)
				.build()) ;
	}
	
	@PatchMapping("/{userId}/disable/{channel}")
	public ResponseEntity<ApiResponse<Void>> disableUserContact(UUID userId, NotificationChannelType channel) {
		channelContactService.disableContact(userId, channel) ;
		
		return ResponseEntity.ok(ApiResponse.<Void>builder()
				.status(HttpStatus.OK)
				.success(true)
				.message(List.of("User contact disabled successfully for channel " + channel))
				.data(null)
				.build()) ;
	}
}
