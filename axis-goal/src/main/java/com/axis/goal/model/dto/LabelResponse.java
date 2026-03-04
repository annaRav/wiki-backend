package com.axis.goal.model.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Response DTO containing label information")
public record LabelResponse(

    @Schema(description = "Unique identifier of the label")
    UUID id,

    @Schema(description = "Display name of the label")
    String displayName,

    @Schema(description = "Color of the label in hex format")
    String color,

    @Schema(description = "ID of the user who owns this label")
    UUID userId

) {
}