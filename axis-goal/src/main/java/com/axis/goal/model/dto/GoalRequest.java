package com.axis.goal.model.dto;

import com.axis.goal.model.entity.Goal.GoalStatus;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "Request DTO for creating or updating a goal")
public record GoalRequest(

    @Schema(description = "Title of the goal")
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    String title,

    @Schema(description = "Detailed description of the goal")
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    String description,

    @Schema(description = "ID of the goal type")
    @NotNull(message = "Goal type ID is required")
    UUID typeId,

    @Schema(description = "Current status of the goal")
    @NotNull(message = "Goal status is required")
    GoalStatus status,

    @Schema(description = "List of custom field answers for this goal")
    @Valid
    List<CustomFieldAnswerRequest> customAnswers

) {
}