package com.axis.goal.controller;

import com.axis.goal.model.dto.CustomFieldDefinitionRequest;
import com.axis.goal.model.dto.CustomFieldDefinitionResponse;
import com.axis.goal.service.CustomFieldDefinitionService;
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
@RequestMapping("/api/goal-types/{goalTypeId}/custom-fields")
@RequiredArgsConstructor
@Tag(name = "Custom Field Definitions", description = "API for managing custom field definitions for goal types")
public class CustomFieldDefinitionController {

    private final CustomFieldDefinitionService definitionService;

    @Operation(
            summary = "Create a custom field definition",
            description = "Creates a new custom field definition for a specific goal type"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Custom field definition created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "404", description = "Goal type not found"),
            @ApiResponse(responseCode = "409", description = "Custom field key already exists")
    })
    @PostMapping
    public ResponseEntity<CustomFieldDefinitionResponse> create(
            @Parameter(description = "Goal Type ID") @PathVariable UUID goalTypeId,
            @Valid @RequestBody CustomFieldDefinitionRequest request) {
        log.debug("Creating custom field definition for goal type: {}", goalTypeId);
        CustomFieldDefinitionResponse response = definitionService.create(goalTypeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Update a custom field definition",
            description = "Updates an existing custom field definition. Only the owner can update."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Custom field definition updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User doesn't have permission"),
            @ApiResponse(responseCode = "404", description = "Custom field definition not found"),
            @ApiResponse(responseCode = "409", description = "Custom field key already exists")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CustomFieldDefinitionResponse> update(
            @Parameter(description = "Goal Type ID") @PathVariable UUID goalTypeId,
            @Parameter(description = "Custom Field Definition ID") @PathVariable UUID id,
            @Valid @RequestBody CustomFieldDefinitionRequest request) {
        log.debug("Updating custom field definition: {}", id);
        CustomFieldDefinitionResponse response = definitionService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get custom field definition by ID",
            description = "Retrieves a specific custom field definition. Only the owner can view."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Custom field definition retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User doesn't have permission"),
            @ApiResponse(responseCode = "404", description = "Custom field definition not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomFieldDefinitionResponse> findById(
            @Parameter(description = "Goal Type ID") @PathVariable UUID goalTypeId,
            @Parameter(description = "Custom Field Definition ID") @PathVariable UUID id) {
        log.debug("Finding custom field definition: {}", id);
        CustomFieldDefinitionResponse response = definitionService.findById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get all custom field definitions for a goal type",
            description = "Retrieves all custom field definitions for a specific goal type"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Custom field definitions retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "404", description = "Goal type not found")
    })
    @GetMapping
    public ResponseEntity<List<CustomFieldDefinitionResponse>> findByGoalTypeId(
            @Parameter(description = "Goal Type ID") @PathVariable UUID goalTypeId) {
        log.debug("Finding custom field definitions for goal type: {}", goalTypeId);
        List<CustomFieldDefinitionResponse> response = definitionService.findByGoalTypeId(goalTypeId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete a custom field definition",
            description = "Deletes a custom field definition. Only the owner can delete."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Custom field definition deleted successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User doesn't have permission"),
            @ApiResponse(responseCode = "404", description = "Custom field definition not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Goal Type ID") @PathVariable UUID goalTypeId,
            @Parameter(description = "Custom Field Definition ID") @PathVariable UUID id) {
        log.debug("Deleting custom field definition: {}", id);
        definitionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}