package com.axis.goal.model.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "Response DTO containing goal type information and its field schema")
public record GoalTypeResponse(

        @Schema(description = "Unique identifier of the goal type")
        UUID id,

        @Schema(description = "Title of the goal type")
        String title,

        @Schema(description = "Hierarchy level number")
        Integer levelNumber,

        @Schema(description = "Schema of custom fields available for this type")
        List<CustomFieldDefinitionResponse> customFields,

        @Schema(description = "ID of the user who owns this configuration")
        UUID userId

) {
}
