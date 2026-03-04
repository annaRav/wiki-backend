package com.axis.goal.model.dto;

import com.axis.goal.model.entity.Goal.GoalStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "Request DTO for creating or updating a goal. For PATCH requests, all fields are optional (only provided fields will be updated).")
public record GoalRequest(

    @Schema(description = "Title of the goal. Required for POST, optional for PATCH.")
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    String title,

    @Schema(description = "Detailed description of the goal. Optional for both POST and PATCH.")
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    String description,

    @Schema(description = "ID of the goal type. Required for POST, optional for PATCH.")
    UUID typeId,

    @Schema(description = "Current status of the goal. Required for POST, optional for PATCH.")
    GoalStatus status,

    @Schema(description = "List of custom field answers for this goal. Optional.")
    @Valid
    List<CustomFieldAnswerRequest> customAnswers,

    @Schema(description = "List of label IDs to attach to this goal. Optional.")
    List<UUID> labelIds

) {
}