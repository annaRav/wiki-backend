package com.axis.goal.model.dto;

import com.axis.goal.model.enums.CustomFieldType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request to define a custom field")
public record CustomFieldDefinitionRequest(
        @Schema(description = "Technical key for the field", example = "budget_limit")
        @NotBlank(message = "Field key is required")
        @Pattern(regexp = "^[a-z0-9_]+$", message = "Key must be snake_case (lowercase, numbers, underscores)")
        String key,

        @Schema(description = "Display label for the user", example = "Обмеження бюджету")
        @NotBlank(message = "Field label is required")
        String label,

        @Schema(description = "Data type of the field")
        @NotNull(message = "Field type is required")
        CustomFieldType type,

        @Schema(description = "Whether the field must be filled")
        boolean required,

        @Schema(description = "Help text for the input field")
        String placeholder
) {}
