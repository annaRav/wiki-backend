package com.axis.user.model.dto;

import com.axis.user.model.entity.UserSettings.DefaultGoalView;
import com.axis.user.model.entity.UserSettings.Theme;
import com.axis.user.model.entity.UserSettings.WeekStartsOn;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserSettingsRequest(
        Theme theme,
        @Size(max = 10) String locale,
        @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Must be a valid hex color (e.g. #1A2B3C)")
        String accentColor,
        Boolean twoFactorEnabled,
        WeekStartsOn weekStartsOn,
        DefaultGoalView defaultGoalView
) {}