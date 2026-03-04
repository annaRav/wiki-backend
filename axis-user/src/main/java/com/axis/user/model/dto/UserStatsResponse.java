package com.axis.user.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserStatsResponse(
        UUID id,
        UUID userId,
        int totalGoals,
        int completedGoals,
        int activeGoals,
        LocalDateTime updatedAt
) {}