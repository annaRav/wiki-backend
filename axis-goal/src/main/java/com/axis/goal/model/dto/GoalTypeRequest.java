package com.axis.goal.model.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Request DTO for creating or updating a goal type (layer configuration)")
public record GoalTypeRequest(

        @Schema(description = "Title of the goal type/layer", example = "Стратегічна ціль")
        @NotBlank(message = "Title is required")
        @Size(max = 100, message = "Title must not exceed 100 characters")
        String title,

        @Schema(description = "List of custom field definitions for this layer")
        @Valid
        List<CustomFieldDefinitionRequest> customFields
) {
}
