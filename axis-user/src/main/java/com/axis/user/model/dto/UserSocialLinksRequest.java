package com.axis.user.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserSocialLinksRequest(
        @Size(max = 100) String telegramUsername,
        Long telegramChatId,
        @Email @Size(max = 255) String email
) {}