package com.axis.notification.model.dto;

import com.axis.notification.model.entity.NotificationLog;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to create a notification log entry")
public record NotificationLogRequest(

        @Schema(description = "Notification content/message", example = "Your goal deadline is approaching")
        @NotBlank(message = "Content cannot be blank")
        @Size(max = 5000, message = "Content must not exceed 5000 characters")
        String content,

        @Schema(description = "Notification delivery channel", example = "EMAIL")
        @NotNull(message = "Channel cannot be null")
        NotificationLog.Channel channel,

        @Schema(description = "Notification status (defaults to SENT if not provided)", example = "SENT")
        NotificationLog.Status status
) {
}