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
@Tag(name = "Goal Types", description = "API для налаштування рівнів цілей та схем кастомних полів")
public class GoalTypeController {

    private final GoalTypeService goalTypeService;

    @Operation(
            summary = "Створити новий тип цілі (рівень)",
            description = "Додає нову конфігурацію шару з кастомними полями для користувача"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Тип цілі успішно створено"),
            @ApiResponse(responseCode = "400", description = "Некоректні вхідні дані"),
            @ApiResponse(responseCode = "401", description = "Користувач не авторизований")
    })
    @PostMapping
    public ResponseEntity<GoalTypeResponse> create(@Valid @RequestBody GoalTypeRequest request) {
        log.debug("Creating new goal type: {}", request.title());
        GoalTypeResponse response = goalTypeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Оновити існуючий тип цілі",
            description = "Оновлює назву, номер рівня або схему кастомних полів"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Тип цілі успішно оновлено"),
            @ApiResponse(responseCode = "404", description = "Тип цілі не знайдено")
    })
    @PutMapping("/{id}")
    public ResponseEntity<GoalTypeResponse> update(
            @Parameter(description = "ID типу цілі") @PathVariable UUID id,
            @Valid @RequestBody GoalTypeRequest request) {
        log.debug("Updating goal type: {}", id);
        GoalTypeResponse response = goalTypeService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Отримати тип цілі за ID",
            description = "Повертає повну схему типу цілі, включаючи описи кастомних полів"
    )
    @GetMapping("/{id}")
    public ResponseEntity<GoalTypeResponse> findById(
            @Parameter(description = "ID типу цілі") @PathVariable UUID id) {
        log.debug("Finding goal type: {}", id);
        GoalTypeResponse response = goalTypeService.findById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Отримати всі типи цілей",
            description = "Повертає список усіх налаштованих рівнів для поточного користувача"
    )
    @GetMapping
    public ResponseEntity<Page<GoalTypeResponse>> findAll(
            @PageableDefault(size = 20, sort = "levelNumber") Pageable pageable) {
        log.debug("Finding all goal types for user");
        Page<GoalTypeResponse> response = goalTypeService.findAll(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Видалити тип цілі",
            description = "Видаляє конфігурацію типу цілі. Увага: це призведе до видалення всіх пов'язаних цілей!"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Тип цілі успішно видалено"),
            @ApiResponse(responseCode = "404", description = "Тип цілі не знайдено")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID типу цілі") @PathVariable UUID id) {
        log.debug("Deleting goal type: {}", id);
        goalTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}