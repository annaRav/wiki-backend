package com.axis.goal.model.dto;

import com.axis.goal.model.enums.CustomFieldType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Response containing custom field definition")
public record CustomFieldDefinitionResponse(
        @Schema(description = "Unique identifier of the field definition")
        UUID id,

        @Schema(description = "Technical key for the field", example = "budget_limit")
        String key,

        @Schema(description = "Display label for the user", example = "Обмеження бюджету")
        String label,

        @Schema(description = "Data type of the field")
        CustomFieldType type,

        @Schema(description = "Whether the field must be filled")
        boolean required,

        @Schema(description = "Help text for the input field")
        String placeholder
) {}
