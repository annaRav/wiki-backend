package com.axis.goal.controller;

import com.axis.goal.model.dto.GoalTypeRequest;
import com.axis.goal.model.dto.GoalTypeResponse;
import com.axis.goal.service.GoalTypeService;
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
@RequestMapping("/api/goal-types")
@RequiredArgsConstructor
@Tag(name = "Goal Types", description = "API for configuring goal levels and custom field schemas")
public class GoalTypeController {

    private final GoalTypeService goalTypeService;

    @Operation(
            summary = "Create new goal type (level)",
            description = "Adds a new layer configuration with custom fields for the user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Goal type successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "User not authorized")
    })
    @PostMapping
    public ResponseEntity<GoalTypeResponse> create(@Valid @RequestBody GoalTypeRequest request) {
        log.debug("Creating new goal type: {}", request.title());
        GoalTypeResponse response = goalTypeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Update goal type",
            description = "Updates the title or custom field schema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Goal type successfully updated"),
            @ApiResponse(responseCode = "404", description = "Goal type not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<GoalTypeResponse> update(
            @Parameter(description = "Goal type ID") @PathVariable UUID id,
            @Valid @RequestBody GoalTypeRequest request) {
        log.debug("Updating goal type: {}", id);
        GoalTypeResponse response = goalTypeService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get goal type by ID",
            description = "Returns the full goal type schema, including custom field definitions"
    )
    @GetMapping("/{id}")
    public ResponseEntity<GoalTypeResponse> findById(
            @Parameter(description = "Goal type ID") @PathVariable UUID id) {
        log.debug("Finding goal type: {}", id);
        GoalTypeResponse response = goalTypeService.findById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get all goal types",
            description = "Returns a list of all configured levels for the current user"
    )
    @GetMapping
    public ResponseEntity<Page<GoalTypeResponse>> findAll(@PageableDefault(size = 20, sort = "levelNumber") Pageable pageable) {
        log.debug("Finding all goal types for user");
        Page<GoalTypeResponse> response = goalTypeService.findAll(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete goal type",
            description = "Deletes the goal type configuration. Warning: this will delete all related goals!"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Goal type successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Goal type not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Goal type ID") @PathVariable UUID id) {
        log.debug("Deleting goal type: {}", id);
        goalTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}