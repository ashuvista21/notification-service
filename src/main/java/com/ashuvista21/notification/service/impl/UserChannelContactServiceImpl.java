package com.ashuvista21.notification.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ashuvista21.notification.entities.NotificationOtp ;
import com.ashuvista21.notification.entities.UserChannelContact;
import com.ashuvista21.notification.enums.NotificationChannelType;
import com.ashuvista21.notification.enums.NotificationType ;
import com.ashuvista21.notification.exceptions.userchannelcontact.UserChannelContactAlreadyExistsException;
import com.ashuvista21.notification.exceptions.userchannelcontact.UserChannelContactAlreadyVerifiedException;
import com.ashuvista21.notification.exceptions.userchannelcontact.UserChannelContactDisabledException;
import com.ashuvista21.notification.exceptions.userchannelcontact.UserChannelContactNotFoundException;
import com.ashuvista21.notification.exceptions.userchannelcontact.UserChannelContactNotVerifiedException;
import com.ashuvista21.notification.exceptions.userchannelcontact.UserChannelContactUnchangedException;
import com.ashuvista21.notification.repository.UserChannelContactRepository;
import com.ashuvista21.notification.service.EventOutboxService ;
import com.ashuvista21.notification.service.UserChannelContactService;
import com.core.otp.dto.OtpGenerateRequest ;
import com.core.otp.service.OtpLifecycleService ;

import jakarta.annotation.Generated ;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserChannelContactServiceImpl implements UserChannelContactService {
	
	private final UserChannelContactRepository userChannelContactRepository ;
	private final EventOutboxService eventOutboxService ;
	private final OtpLifecycleService<NotificationOtp> otpLifecycleService ;
	
	@Override
	@Transactional
	public void updateContact(UUID userId, NotificationChannelType channelType, String contact) {
		// get existing channel contact record for user
		UserChannelContact channelContact = userChannelContactRepository.findByUserIdAndChannel(userId, channelType)
				.orElseThrow(() -> new UserChannelContactNotFoundException("User contact not found for " + channelType)) ;
		
		// check for same contact value
		if(channelContact.getValue().equals(contact))
			throw new UserChannelContactUnchangedException("Contact is already set to the same value for channel " + channelType) ;
		
		// update channel contact via dirty checking
		channelContact.setValue(contact) ;
		channelContact.setVerified(false) ;
	}

	@Override
	@Transactional
	public void addContact(UUID userId, NotificationChannelType channelType, String contact) {
		// get already existing record if available
		boolean alreadyExists = userChannelContactRepository.existsByUserIdAndChannel(userId, channelType) ;
		
		// if available throw exception
		if(alreadyExists)
			throw new UserChannelContactAlreadyExistsException("User Contact Already Exists for " + channelType + "  type") ;
		
		// Build UserChannelContact Object
		UserChannelContact userChannelContact = UserChannelContact.builder()
				.userId(userId)
				.channel(channelType)
				.value(contact)
				.build() ;
		
		// save entity
		userChannelContactRepository.save(userChannelContact) ;
	}

	@Override
	@Transactional
	public void disableContact(UUID userId, NotificationChannelType channelType) {
		// get existing channel contact record for user
		UserChannelContact channelContact = userChannelContactRepository.findByUserIdAndChannel(userId, channelType)
				.orElseThrow(() -> new UserChannelContactNotFoundException("User contact not found for " + channelType)) ;
		
		// update enable flag via dirty checking
		channelContact.setEnabledFlag(false) ;
		
	}

	@Override
	@Transactional
	public void enableContact(UUID userId, NotificationChannelType channelType) {
		// get existing channel contact record for user
		UserChannelContact channelContact = userChannelContactRepository.findByUserIdAndChannel(userId, channelType)
				.orElseThrow(() -> new UserChannelContactNotFoundException("User contact not found for " + channelType)) ;
				
		// update enable flag via dirty checking
		channelContact.setEnabledFlag(true) ;
	}

	@Override
	@Transactional
	public void makePrimaryContact(UUID userId, NotificationChannelType channelType) {
		// get existing channel contact record for user
		UserChannelContact channelContact = userChannelContactRepository.findByUserIdAndChannel(userId, channelType)
				.orElseThrow(() -> new UserChannelContactNotFoundException("User contact not found for " + channelType)) ;
						
		// update enable flag via dirty checking
		channelContact.setPrimaryContact(true) ;
	}

	@Override
	public UserChannelContact getVerifiedUserChannelContact(UUID userId, NotificationChannelType channelType) {
		// get existing channel contact record for user
		UserChannelContact contact = userChannelContactRepository
	            .findByUserIdAndChannel(userId, channelType)
	            .orElseThrow(() -> new UserChannelContactNotFoundException(
	                    "User contact not found for " + channelType)) ;

	    // check verified flag
	    if(!Boolean.TRUE.equals(contact.getVerified())) {
	        throw new UserChannelContactNotVerifiedException(
	                "User contact not verified for " + channelType) ;
	    }

	    return contact ;
	}

	@Override
	public void triggerUserChannelContactVerification(UUID userId, NotificationChannelType channelType) {
		// get existing channel contact record for user
		UserChannelContact channelContact = userChannelContactRepository
	            .findByUserIdAndChannel(userId, channelType)
			    .orElseThrow(() -> new UserChannelContactNotFoundException(
			    		"User contact not found for " + channelType)) ;
		
		if(channelContact.getVerified())
			throw new UserChannelContactAlreadyVerifiedException("User channel contact is already verified for " + channelType) ;
		if(!channelContact.getEnabledFlag())
			throw new UserChannelContactDisabledException("User channel contact is disabled for " + channelType) ;
		
		OtpGenerateRequest request = new OtpGenerateRequest(
				channelContact.getId().toString(),
				NotificationType.CHANNEL_VERIFICATION.toString(),
				NotificationType.CHANNEL_VERIFICATION.getOtpCharsType().toString()) ;
		otpLifecycleService.generate(request) ;
		
		//send notification
		eventOutboxService.createEvent(
				"CHANNEL_CONTACT",
				request.referenceId(),
				"notification-channel-verification",
				request) ;
	}

}
