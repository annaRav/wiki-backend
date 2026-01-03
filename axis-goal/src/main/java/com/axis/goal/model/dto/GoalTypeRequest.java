package com.axis.goal.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Request DTO for creating or updating a goal type (layer configuration)")
public record GoalTypeRequest(

        @Schema(description = "Title of the goal type/layer", example = "Стратегічна ціль")
        @NotBlank(message = "Title is required")
        @Size(max = 100, message = "Title must not exceed 100 characters")
        String title,

        @Schema(description = "Hierarchy level number (1 for root, 2 for sub-goals, etc.)", example = "1")
        @NotNull(message = "Level number is required")
        @Min(value = 1, message = "Level number must be at least 1")
        Integer levelNumber,

        @Schema(description = "List of custom field definitions for this layer")
        @Valid
        List<CustomFieldDefinitionRequest> customFields
) {
}
