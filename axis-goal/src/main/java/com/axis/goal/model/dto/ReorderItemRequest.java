package com.axis.goal.model.dto;

import jakarta.validation.constraints.Min;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Request DTO for moving a checklist item to a new position")
public record ReorderItemRequest(

    @Schema(description = "New zero-based position for the item")
    @Min(value = 0, message = "Position must be >= 0")
    int position

) {
}
