package com.axis.goal.model.dto;

import com.axis.goal.model.enums.CustomFieldType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Response containing custom field answer with definition metadata")
public record CustomFieldAnswerResponse(
        @Schema(description = "Unique identifier of the answer")
        UUID id,

        @Schema(description = "ID of the custom field definition")
        UUID fieldDefinitionId,

        @Schema(description = "Technical key of the field", example = "budget_limit")
        String fieldKey,

        @Schema(description = "Display label of the field", example = "Budget Limit")
        String fieldLabel,

        @Schema(description = "Data type of the field")
        CustomFieldType fieldType,

        @Schema(description = "Value provided for the field", example = "5000")
        String value
) {}