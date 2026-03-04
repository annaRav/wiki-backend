package com.axis.goal.model.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response DTO containing checklist item information")
public record ChecklistItemResponse(

    @Schema(description = "Unique identifier of the checklist item")
    UUID id,

    @Schema(description = "Title of the checklist item")
    String title,

    @Schema(description = "Whether the item is completed")
    boolean completed,

    @Schema(description = "Display order position of the item")
    int position,

    @Schema(description = "Timestamp when the item was created")
    LocalDateTime createdAt,

    @Schema(description = "Timestamp when the item was last updated")
    LocalDateTime updatedAt

) {
}
