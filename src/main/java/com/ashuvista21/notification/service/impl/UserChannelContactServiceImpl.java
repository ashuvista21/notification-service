package com.ashuvista21.notification.service.impl;

import java.util.List ;
import java.util.Map ;
import java.util.Optional ;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ashuvista21.notification.config.NotificationChannelProperties ;
import com.ashuvista21.notification.dtos.NotificationInboundEvent ;
import com.ashuvista21.notification.dtos.OTPEvent ;
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
import com.core.otp.dto.GeneratedOtp ;
import com.core.otp.dto.OtpGenerateRequest ;
import com.core.otp.dto.OtpValidationRequest ;
import com.core.otp.exceptions.OtpAlreadyConsumedException ;
import com.core.otp.exceptions.OtpExpiredException ;
import com.core.otp.exceptions.OtpInvalidException ;
import com.core.otp.exceptions.OtpPurposeMismatchException ;
import com.core.otp.exceptions.OtpRetryExceededException ;
import com.core.otp.service.OtpLifecycleService ;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserChannelContactServiceImpl implements UserChannelContactService {
	
	private final UserChannelContactRepository userChannelContactRepository ;
	private final EventOutboxService eventOutboxService ;
	private final OtpLifecycleService<NotificationOtp> otpLifecycleService ;
	private final NotificationChannelProperties channelProperties ;
	
	@Override
	@Transactional
	public void updateContact(UUID userId, NotificationChannelType channelType, String contact, boolean createIfAbsent) {
		// get existing channel contact record for user
		Optional<UserChannelContact> optionalContact =
		        userChannelContactRepository.findByUserIdAndChannel(userId, channelType) ;

		UserChannelContact channelContact ;

		if (optionalContact.isPresent()) {
		    channelContact = optionalContact.get() ;
		} else if (createIfAbsent) {
		    addContact(userId, channelType, contact, false) ;
		    return ;
		} else {
		    throw new UserChannelContactNotFoundException(
		            "User contact not found for " + channelType);
		}
		
		// check for same contact value
		if(channelContact.getValue().equals(contact))
			throw new UserChannelContactUnchangedException("Contact is already set to the same value for channel " + channelType) ;
		
		// update channel contact via dirty checking
		channelContact.setValue(contact) ;
		channelContact.setVerified(false) ;
	}

	@Override
	@Transactional
	public void addContact(UUID userId, NotificationChannelType channelType, String contact, boolean overrideFlag) {
		// get already existing record if available
		boolean alreadyExists = userChannelContactRepository.existsByUserIdAndChannel(userId, channelType) ;
		
		// if available throw exception
		if(alreadyExists) {
			if(!overrideFlag)
				throw new UserChannelContactAlreadyExistsException("User Contact Already Exists for " + channelType + "  type") ;
			updateContact(userId, channelType, contact, false) ;
			return ;
		}
		
		// Build UserChannelContact Object
		UserChannelContact userChannelContact = UserChannelContact.builder()
				.userId(userId)
				.channel(channelType)
				.value(contact)
				.enabledFlag(true)
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
	public UserChannelContact getUserChannelContact(UUID userId, NotificationChannelType channelType, boolean checkVerifiedflag) {
		// get existing channel contact record for user
		UserChannelContact contact = userChannelContactRepository
	            .findByUserIdAndChannel(userId, channelType)
	            .orElseThrow(() -> new UserChannelContactNotFoundException(
	                    "User contact not found for " + channelType)) ;
	    // check verified flag
	    if(checkVerifiedflag && !Boolean.TRUE.equals(contact.getVerified())) {
	        throw new UserChannelContactNotVerifiedException(
	                "User contact not verified for " + channelType) ;
	    }

	    return contact ;
	}
	
	@Override
	public List<UserChannelContact> getUserChannelContacts(UUID userId) {
		// get existing channel contact record for user
		List<UserChannelContact> contacts = userChannelContactRepository
	            .findByUserId(userId) ;

	    return contacts ;
	}

	@Override
	@Transactional
	public String triggerUserChannelContactVerification(UUID userId, NotificationChannelType channelType) {
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
		GeneratedOtp generatedOtp = otpLifecycleService.generate(request) ;
		OTPEvent otpEvent = new OTPEvent(
				generatedOtp.requestId(),
				generatedOtp.otp(),
				generatedOtp.expiry(),
				generatedOtp.unitTime(),
				userId.toString(),
				NotificationType.CHANNEL_VERIFICATION.toString(),
				"CHANNEL_CONTACT_" + channelType.toString()) ;
		
		//send notification
		eventOutboxService.createEvent(
				"CHANNEL_CONTACT_" + channelType.toString(),
				request.referenceId(),
				channelProperties.getChannelVerificationTopic(),
				otpEvent) ;
		
		return generatedOtp.requestId() ;
	}

	@Override
	@Transactional
	public void verifyUserChannelContact(UUID requestId, UUID userId, NotificationChannelType channel, String otp) {
		UserChannelContact channelContact = userChannelContactRepository
	            .findByUserIdAndChannel(userId, channel)
			    .orElseThrow(() -> new UserChannelContactNotFoundException(
			    		"User contact not found for " + channel)) ;
		
		if(channelContact.getVerified())
			throw new UserChannelContactAlreadyVerifiedException("User channel contact is already verified for " + channel.toString()) ;
		if(!channelContact.getEnabledFlag())
			throw new UserChannelContactDisabledException("User channel contact is disabled for " + channel.toString()) ;
		
		NotificationOtp otpDetails = otpLifecycleService.getOtpDetails(requestId) ;
		
		if(otpDetails == null || !otpDetails.getEventRefId().equals(channelContact.getId().toString()))
			throw new OtpInvalidException("No OTP request found for the user contact for channel " + channel.toString()) ;
		if(!otpDetails.getOtpPurpose().equals(NotificationType.CHANNEL_VERIFICATION.toString()))
			throw new OtpPurposeMismatchException("OTP request found for the user contact for channel " + channel.toString() + " but for a different purpose") ;
		if(otpDetails.getConsumed())
			throw new OtpAlreadyConsumedException("OTP has already been consumed for the user contact for channel " + channel.toString()) ;
		if(otpDetails.getExpiryAt().isBefore(java.time.Instant.now()))
			throw new OtpExpiredException("OTP has expired for user contact for channel " + channel.toString()) ;
		if(otpDetails.getRetryCount() >= otpLifecycleService.getMaxRetryCount())
			throw new OtpRetryExceededException("Maximum retry attempts exceeded for OTP verification for user contact for channel " + channel.toString()) ;
		
		OtpValidationRequest request = new OtpValidationRequest(
				otp,
				otpDetails.getOtpPurpose(),
				requestId.toString(),
				channelContact.getId().toString()) ;
		
		otpLifecycleService.validate(request) ;
		channelContact.setVerified(true) ;
		
		NotificationInboundEvent inboundEvent = new NotificationInboundEvent(
				channelContact.getId().toString(),
				channelContact.getUserId().toString(),
				NotificationType.CHANNEL_VERIFICATION_SUCCESS.toString(),
				"CHANNEL_CONTACT_" + channel.toString(),
				Map.of("type", NotificationType.CHANNEL_VERIFICATION_SUCCESS.toString())) ;
		
		eventOutboxService.createEvent(
				"CHANNEL_CONTACT_" + channel.toString(),
				channelContact.getId().toString(),
				channelProperties.getInboundTopic(),
				inboundEvent) ;
	}
}
