package com.axis.goal.model.dto;

import com.axis.goal.model.enums.ProgressStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "Request DTO for creating or updating a goal")
public record GoalRequest(

    @Schema(description = "Title of the goal. Required for POST, optional for PATCH.")
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    String title,

    @Schema(description = "Detailed description of the goal.")
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    String description,

    @Schema(description = "ID of the life aspect this goal belongs to. Required for POST.")
    @NotNull(message = "Life aspect ID is required")
    UUID lifeAspectId,

    @Schema(description = "Progress status of the goal.")
    ProgressStatus status,

    @Schema(description = "List of label IDs to attach to this goal.")
    List<UUID> labelIds

) {}
