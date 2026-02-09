package com.axis.notification.model.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response containing notification settings")
public record NotificationSettingsResponse(

        @Schema(description = "Unique settings ID", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "User ID", example = "123e4567-e89b-12d3-a456-426614174001")
        UUID userId,

        @Schema(description = "Email notifications enabled", example = "true")
        Boolean enableEmail,

        @Schema(description = "Push notifications enabled", example = "true")
        Boolean enablePush,

        @Schema(description = "Telegram notifications enabled", example = "false")
        Boolean enableTelegram,

        @Schema(description = "Timestamp when settings were created", example = "2026-01-23T10:15:30")
        LocalDateTime createdAt,

        @Schema(description = "Timestamp when settings were last updated", example = "2026-01-23T10:15:30")
        LocalDateTime updatedAt
) {
}