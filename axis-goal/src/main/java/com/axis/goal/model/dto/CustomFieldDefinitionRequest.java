package com.axis.goal.model.dto;

import com.axis.goal.model.enums.CustomFieldType;
import com.axis.goal.model.enums.OwnerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Request to define a custom field for a specific entity type")
public record CustomFieldDefinitionRequest(

        @Schema(description = "Display label for the user")
        @NotBlank(message = "Field label is required")
        String label,

        @Schema(description = "Data type of the field")
        @NotNull(message = "Field type is required")
        CustomFieldType type,

        @Schema(description = "Whether the field must be filled")
        boolean required,

        @Schema(description = "Help text for the input field")
        String placeholder,

        @Schema(description = "Entity type this field definition belongs to (LIFE_ASPECT, GOAL, SUB_GOAL)")
        @NotNull(message = "Owner type is required")
        OwnerType ownerType

) {}
