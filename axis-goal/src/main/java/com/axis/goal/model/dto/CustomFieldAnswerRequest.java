package com.axis.goal.model.dto;

import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Request to provide an answer to a custom field")
public record CustomFieldAnswerRequest(

        @Schema(description = "ID of the owner entity (goal, life aspect, sub-goal)")
        @NotNull(message = "Owner ID is required")
        UUID ownerId,

        @Schema(description = "ID of the custom field definition")
        @NotNull(message = "Field definition ID is required")
        UUID fieldDefinitionId,

        @Schema(description = "Value for the custom field")
        String value

) {}
