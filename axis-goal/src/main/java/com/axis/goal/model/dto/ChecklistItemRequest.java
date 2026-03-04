package com.axis.goal.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Request DTO for creating or updating a checklist item")
public record ChecklistItemRequest(

    @Schema(description = "Title of the checklist item. Required for POST, optional for PATCH.")
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    String title,

    @Schema(description = "Whether the item is completed. Optional for PATCH.")
    Boolean completed

) {
}
