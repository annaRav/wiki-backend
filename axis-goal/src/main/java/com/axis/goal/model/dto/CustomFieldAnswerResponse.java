package com.axis.goal.model.dto;

import com.axis.goal.model.enums.CustomFieldType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Response containing custom field answer with definition metadata")
public record CustomFieldAnswerResponse(
        @Schema(description = "Unique identifier of the answer")
        UUID id,

        @Schema(description = "ID of the custom field definition")
        UUID fieldDefinitionId,

        @Schema(description = "Display label of the field")
        String fieldLabel,

        @Schema(description = "Data type of the field")
        CustomFieldType fieldType,

        @Schema(description = "Value provided for the field")
        String value
) {}