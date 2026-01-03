package com.axis.goal.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Request to provide an answer to a custom field")
public record CustomFieldAnswerRequest(
        @Schema(description = "ID of the custom field definition")
        @NotNull(message = "Field definition ID is required")
        UUID fieldDefinitionId,

        @Schema(description = "Value for the custom field", example = "5000")
        String value
) {}