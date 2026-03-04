package com.axis.goal.model.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Response DTO containing checklist information")
public record ChecklistResponse(

    @Schema(description = "Unique identifier of the checklist")
    UUID id,

    @Schema(description = "Title of the checklist")
    String title,

    @Schema(description = "Display order position of the checklist")
    int position,

    @Schema(description = "Items in this checklist, ordered by position")
    List<ChecklistItemResponse> items,

    @Schema(description = "Timestamp when the checklist was created")
    LocalDateTime createdAt,

    @Schema(description = "Timestamp when the checklist was last updated")
    LocalDateTime updatedAt

) {
}
