package com.axis.user.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserSocialLinksResponse(
        UUID id,
        UUID userId,
        String telegramUsername,
        Long telegramChatId,
        String email,
        LocalDateTime updatedAt
) {}