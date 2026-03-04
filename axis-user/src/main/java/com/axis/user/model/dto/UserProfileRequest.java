package com.axis.user.model.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserProfileRequest(
        @Size(max = 100) String displayName,
        @Size(max = 500) String bio,
        @Size(max = 500) String avatarUrl,
        @Size(max = 50) String timezone,
        LocalDate dateOfBirth
) {}