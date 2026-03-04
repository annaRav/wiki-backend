package com.axis.user.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        UUID userId,
        String displayName,
        String bio,
        String avatarUrl,
        String timezone,
        LocalDate dateOfBirth,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}