package com.axis.notification.model.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to create or update notification settings")
public record NotificationSettingsRequest(

        @Schema(description = "Enable email notifications", example = "true")
        @NotNull(message = "Email notification preference cannot be null")
        Boolean enableEmail,

        @Schema(description = "Enable push notifications", example = "true")
        @NotNull(message = "Push notification preference cannot be null")
        Boolean enablePush,

        @Schema(description = "Enable Telegram notifications", example = "false")
        @NotNull(message = "Telegram notification preference cannot be null")
        Boolean enableTelegram
) {
}