package com.axis.goal.controller;

import com.axis.goal.model.dto.GoalRequest;
import com.axis.goal.model.dto.GoalResponse;
import com.axis.goal.model.entity.Goal.GoalStatus;
import com.axis.goal.model.entity.GoalType;
import com.axis.goal.service.GoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@Tag(name = "Goals", description = "Goal management API for creating, tracking, and organizing life goals")
public class GoalController {

    private final GoalService goalService;

    @Operation(
        summary = "Create a new goal",
        description = "Creates a new goal for the authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Goal created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @PostMapping
    public ResponseEntity<GoalResponse> create(@Valid @RequestBody GoalRequest request) {
        log.debug("Creating new goal");
        GoalResponse response = goalService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "Update an existing goal",
        description = "Updates an existing goal. Only the owner can update their goals."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Goal updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "404", description = "Goal not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> update(
            @Parameter(description = "Goal ID") @PathVariable UUID id,
            @Valid @RequestBody GoalRequest request) {
        log.debug("Updating goal: {}", id);
        GoalResponse response = goalService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get goal by ID",
        description = "Retrieves a specific goal by its ID. Only the owner can view their goals."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Goal retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "404", description = "Goal not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GoalResponse> findById(
            @Parameter(description = "Goal ID") @PathVariable UUID id) {
        log.debug("Finding goal: {}", id);
        GoalResponse response = goalService.findById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get all goals",
        description = "Retrieves all goals for the authenticated user with pagination"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Goals retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @GetMapping
    public ResponseEntity<Page<GoalResponse>> findAll(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        log.debug("Finding all goals");
        Page<GoalResponse> response = goalService.findAll(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get goals by status",
        description = "Retrieves goals filtered by status for the authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Goals retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<GoalResponse>> findByStatus(
            @Parameter(description = "Goal status") @PathVariable GoalStatus status,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        log.debug("Finding goals with status: {}", status);
        Page<GoalResponse> response = goalService.findByStatus(status, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get goals by type",
        description = "Retrieves goals filtered by type (LONG_TERM, MEDIUM_TERM, SHORT_TERM) for the authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Goals retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @GetMapping("/type/{type}")
    public ResponseEntity<Page<GoalResponse>> findByType(
            @Parameter(description = "Goal type") @PathVariable GoalType type,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        log.debug("Finding goals with type: {}", type);
        Page<GoalResponse> response = goalService.findByType(type, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Delete a goal",
        description = "Deletes a goal. Only the owner can delete their goals."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Goal deleted successfully"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "404", description = "Goal not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Goal ID") @PathVariable UUID id) {
        log.debug("Deleting goal: {}", id);
        goalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}