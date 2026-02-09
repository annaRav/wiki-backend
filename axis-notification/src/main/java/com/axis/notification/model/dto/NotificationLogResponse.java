package com.axis.notification.model.dto;

import com.axis.notification.model.entity.NotificationLog;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response containing notification log details")
public record NotificationLogResponse(

        @Schema(description = "Unique notification ID", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "User ID who received the notification", example = "123e4567-e89b-12d3-a456-426614174001")
        UUID userId,

        @Schema(description = "Notification content/message", example = "Your goal deadline is approaching")
        String content,

        @Schema(description = "Notification delivery channel", example = "EMAIL")
        NotificationLog.Channel channel,

        @Schema(description = "Notification status", example = "SENT")
        NotificationLog.Status status,

        @Schema(description = "Timestamp when notification was created", example = "2026-01-23T10:15:30")
        LocalDateTime createdAt,

        @Schema(description = "Timestamp when notification was last updated", example = "2026-01-23T10:15:30")
        LocalDateTime updatedAt
) {
}