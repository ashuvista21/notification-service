package com.ashuvista21.notification.controller;

import java.util.List ;
import java.util.UUID ;

import org.springframework.http.HttpStatus ;
import org.springframework.http.ResponseEntity ;
import org.springframework.web.bind.annotation.PatchMapping ;
import org.springframework.web.bind.annotation.PathVariable ;
import org.springframework.web.bind.annotation.RequestBody ;
import org.springframework.web.bind.annotation.RequestMapping ;
import org.springframework.web.bind.annotation.RestController ;

import com.ashuvista21.notification.dtos.ApiResponse ;
import com.ashuvista21.notification.dtos.EventOutboxRequest ;
import com.ashuvista21.notification.service.EventOutboxService ;
import com.ashuvista21.notification.utils.ValidatorUtils ;

import lombok.RequiredArgsConstructor ;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/events")
public class EventOutboxController {
	
	private final EventOutboxService eventOutboxService ;
	
	@PatchMapping("/outbox/{eventId}/status")
	public ResponseEntity<ApiResponse<Void>> updateOutboxStatus(
			@PathVariable String eventId,
			@RequestBody EventOutboxRequest request) {
		UUID eventOutboxId = ValidatorUtils.validateUuidAndGetUuid(eventId) ;
		if(request.success()) {
			eventOutboxService.markAsPublished(eventOutboxId) ;
		}
		else {
			eventOutboxService.markAsFailed(eventOutboxId) ;
		}
		
		return ResponseEntity.ok(ApiResponse.<Void>builder()
				.success(true)
				.status(HttpStatus.OK)
				.message(List.of("Event status updated"))
				.data(null)
				.build()) ;
	}
}
