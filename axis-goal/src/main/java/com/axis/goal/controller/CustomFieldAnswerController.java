package com.axis.goal.controller;

import com.axis.goal.model.dto.CustomFieldAnswerRequest;
import com.axis.goal.model.dto.CustomFieldAnswerResponse;
import com.axis.goal.service.CustomFieldAnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/goals/{goalId}/custom-field-answers")
@RequiredArgsConstructor
@Tag(name = "Custom Field Answers", description = "API for managing custom field answers for goals")
public class CustomFieldAnswerController {

    private final CustomFieldAnswerService answerService;

    @Operation(
            summary = "Create a custom field answer",
            description = "Creates a new custom field answer for a specific goal"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Custom field answer created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or field doesn't belong to goal's type"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "404", description = "Goal or field definition not found"),
            @ApiResponse(responseCode = "409", description = "Answer already exists for this field")
    })
    @PostMapping
    public ResponseEntity<CustomFieldAnswerResponse> create(
            @Parameter(description = "Goal ID") @PathVariable UUID goalId,
            @Valid @RequestBody CustomFieldAnswerRequest request) {
        log.debug("Creating custom field answer for goal: {}", goalId);
        CustomFieldAnswerResponse response = answerService.create(goalId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Update a custom field answer",
            description = "Updates an existing custom field answer. Only the owner can update."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Custom field answer updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User doesn't have permission"),
            @ApiResponse(responseCode = "404", description = "Custom field answer not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CustomFieldAnswerResponse> update(
            @Parameter(description = "Goal ID") @PathVariable UUID goalId,
            @Parameter(description = "Custom Field Answer ID") @PathVariable UUID id,
            @Valid @RequestBody CustomFieldAnswerRequest request) {
        log.debug("Updating custom field answer: {}", id);
        CustomFieldAnswerResponse response = answerService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get custom field answer by ID",
            description = "Retrieves a specific custom field answer. Only the owner can view."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Custom field answer retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User doesn't have permission"),
            @ApiResponse(responseCode = "404", description = "Custom field answer not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomFieldAnswerResponse> findById(
            @Parameter(description = "Goal ID") @PathVariable UUID goalId,
            @Parameter(description = "Custom Field Answer ID") @PathVariable UUID id) {
        log.debug("Finding custom field answer: {}", id);
        CustomFieldAnswerResponse response = answerService.findById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get all custom field answers for a goal",
            description = "Retrieves all custom field answers for a specific goal"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Custom field answers retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "404", description = "Goal not found")
    })
    @GetMapping
    public ResponseEntity<List<CustomFieldAnswerResponse>> findByGoalId(
            @Parameter(description = "Goal ID") @PathVariable UUID goalId) {
        log.debug("Finding custom field answers for goal: {}", goalId);
        List<CustomFieldAnswerResponse> response = answerService.findByGoalId(goalId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete a custom field answer",
            description = "Deletes a custom field answer. Only the owner can delete. Cannot delete required fields."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Custom field answer deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Cannot delete required field answer"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User doesn't have permission"),
            @ApiResponse(responseCode = "404", description = "Custom field answer not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Goal ID") @PathVariable UUID goalId,
            @Parameter(description = "Custom Field Answer ID") @PathVariable UUID id) {
        log.debug("Deleting custom field answer: {}", id);
        answerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}