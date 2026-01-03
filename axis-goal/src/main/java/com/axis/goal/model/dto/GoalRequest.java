package com.axis.goal.model.dto;

import com.axis.goal.model.entity.Goal.GoalStatus;
import com.axis.goal.model.entity.GoalType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Request DTO for creating or updating a goal")
public record GoalRequest(

    @Schema(description = "Title of the goal", example = "Learn Spring Boot")
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    String title,

    @Schema(description = "Detailed description of the goal", example = "Master Spring Boot 3 and build microservices")
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    String description,

    @Schema(description = "Type of the goal", example = "MEDIUM_TERM")
    @NotNull(message = "Goal type is required")
    GoalType type,

    @Schema(description = "Current status of the goal", example = "NOT_STARTED")
    @NotNull(message = "Goal status is required")
    GoalStatus status,

    @Schema(description = "List of custom field answers for this goal")
    @Valid
    List<CustomFieldAnswerRequest> customAnswers

) {
}