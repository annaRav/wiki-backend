package com.axis.goal.model.dto;

import com.axis.goal.model.enums.ProgressStatus;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Response DTO containing sub-goal information")
public record SubGoalResponse(

    @Schema(description = "Unique identifier of the sub-goal")
    UUID id,

    @Schema(description = "Title of the sub-goal")
    String title,

    @Schema(description = "Description of the sub-goal")
    String description,

    @Schema(description = "Progress status of the sub-goal")
    ProgressStatus status,

    @Schema(description = "ID of the parent goal")
    UUID goalId,

    @Schema(description = "ID of the user who owns this sub-goal")
    UUID userId,

    @Schema(description = "Timestamp when the sub-goal was created")
    LocalDateTime createdAt,

    @Schema(description = "Timestamp when the sub-goal was last updated")
    LocalDateTime updatedAt,

    @Schema(description = "Labels attached to this sub-goal")
    List<LabelResponse> labels

) {}
