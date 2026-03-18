package com.axis.goal.model.dto;

import com.axis.goal.model.enums.ProgressStatus;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Response DTO containing goal information")
public record GoalResponse(

    @Schema(description = "Unique identifier of the goal")
    UUID id,

    @Schema(description = "Title of the goal")
    String title,

    @Schema(description = "Detailed description of the goal")
    String description,

    @Schema(description = "ID of the life aspect this goal belongs to")
    UUID lifeAspectId,

    @Schema(description = "Progress status of the goal")
    ProgressStatus status,

    @Schema(description = "ID of the user who owns this goal")
    UUID userId,

    @Schema(description = "Timestamp when the goal was created")
    LocalDateTime createdAt,

    @Schema(description = "Timestamp when the goal was last updated")
    LocalDateTime updatedAt,

    @Schema(description = "Labels attached to this goal")
    List<LabelResponse> labels

) {}
