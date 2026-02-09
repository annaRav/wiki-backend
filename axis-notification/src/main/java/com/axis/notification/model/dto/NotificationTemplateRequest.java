package com.axis.notification.model.dto;

import com.axis.notification.model.entity.NotificationTemplates;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to create or update a notification template")
public record NotificationTemplateRequest(

        @Schema(description = "Type of notification template", example = "SMART_GOAL_DEADLINE")
        @NotNull(message = "Template type cannot be null")
        NotificationTemplates.Type type,

        @Schema(description = "Template for notification title", example = "Goal Deadline Reminder: {goalTitle}")
        @NotBlank(message = "Title template cannot be blank")
        @Size(max = 500, message = "Title template must not exceed 500 characters")
        String titleTemplate,

        @Schema(description = "Template for notification body/content", example = "Your goal '{goalTitle}' is due on {deadline}. Current progress: {progress}%")
        @NotBlank(message = "Body template cannot be blank")
        @Size(max = 5000, message = "Body template must not exceed 5000 characters")
        String bodyTemplate
) {
}