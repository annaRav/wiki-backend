package com.axis.goal.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "Request DTO for creating or updating a life aspect")
public record LifeAspectRequest(

    @Schema(description = "Title of the life aspect. Required for POST, optional for PATCH.")
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    String title,

    @Schema(description = "Description of the life aspect.")
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    String description,

    @Schema(description = "Rated status — satisfaction score from 1 (low) to 10 (excellent). Optional.")
    @Min(value = 1, message = "Rated status must be at least 1")
    @Max(value = 10, message = "Rated status must be at most 10")
    Integer ratedStatus,

    @Schema(description = "List of label IDs to attach.")
    List<UUID> labelIds

) {}
