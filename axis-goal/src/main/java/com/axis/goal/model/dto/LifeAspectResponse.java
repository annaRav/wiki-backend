package com.axis.goal.model.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Response DTO containing life aspect information")
public record LifeAspectResponse(

    @Schema(description = "Unique identifier of the life aspect")
    UUID id,

    @Schema(description = "Title of the life aspect")
    String title,

    @Schema(description = "Description of the life aspect")
    String description,

    @Schema(description = "Rated status — satisfaction score from 1 to 10")
    Integer ratedStatus,

    @Schema(description = "ID of the user who owns this life aspect")
    UUID userId,

    @Schema(description = "Timestamp when the life aspect was created")
    LocalDateTime createdAt,

    @Schema(description = "Timestamp when the life aspect was last updated")
    LocalDateTime updatedAt,

    @Schema(description = "Labels attached to this life aspect")
    List<LabelResponse> labels

) {}
