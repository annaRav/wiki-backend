package com.axis.notification.model.dto;

import com.axis.notification.model.entity.NotificationTemplates;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response containing notification template details")
public record NotificationTemplateResponse(

        @Schema(description = "Unique template ID", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Type of notification template", example = "SMART_GOAL_DEADLINE")
        NotificationTemplates.Type type,

        @Schema(description = "Template for notification title", example = "Goal Deadline Reminder: {goalTitle}")
        String titleTemplate,

        @Schema(description = "Template for notification body/content", example = "Your goal '{goalTitle}' is due on {deadline}. Current progress: {progress}%")
        String bodyTemplate,

        @Schema(description = "Timestamp when template was created", example = "2026-01-23T10:15:30")
        LocalDateTime createdAt,

        @Schema(description = "Timestamp when template was last updated", example = "2026-01-23T10:15:30")
        LocalDateTime updatedAt
) {
}