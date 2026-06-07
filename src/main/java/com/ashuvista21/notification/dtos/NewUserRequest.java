package com.ashuvista21.notification.dtos;

import java.util.List ;

import jakarta.validation.constraints.NotBlank ;
import jakarta.validation.constraints.NotEmpty ;

public record NewUserRequest(
        String name,
        @NotBlank(message = "User ID cannot be empty")
        String userId,
        @NotEmpty(message = "At least one channel contact detail is required")
        List<ChannelContactDetails> channelContacts
) {

    public record ChannelContactDetails(
            String channelType,
            String contactValue
    ) {}
}
