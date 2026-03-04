package com.axis.user.model.dto;

import com.axis.user.model.entity.UserSettings.DefaultGoalView;
import com.axis.user.model.entity.UserSettings.Theme;
import com.axis.user.model.entity.UserSettings.WeekStartsOn;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserSettingsResponse(
        UUID id,
        UUID userId,
        Theme theme,
        String locale,
        String accentColor,
        boolean twoFactorEnabled,
        WeekStartsOn weekStartsOn,
        DefaultGoalView defaultGoalView,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}