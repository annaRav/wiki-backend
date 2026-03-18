package com.axis.goal.model.dto;

import com.axis.goal.model.enums.OwnerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Request DTO for creating or updating a checklist")
public record ChecklistRequest(

    @Schema(description = "ID of the owner entity (goal or sub-goal). Required for POST.")
    @NotNull(message = "Owner ID is required")
    UUID ownerId,

    @Schema(description = "Owner type (GOAL, SUB_GOAL). Required for POST.")
    @NotNull(message = "Owner type is required")
    OwnerType ownerType,

    @Schema(description = "Title of the checklist. Required for POST, optional for PATCH.")
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    String title

) {
}
