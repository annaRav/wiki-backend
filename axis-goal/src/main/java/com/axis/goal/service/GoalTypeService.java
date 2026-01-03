package com.axis.goal.service;

import com.axis.goal.model.dto.GoalTypeRequest;
import com.axis.goal.model.dto.GoalTypeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GoalTypeService {

    /**
     * Створити новий тип цілі для автентифікованого користувача
     */
    GoalTypeResponse create(GoalTypeRequest request);

    /**
     * Оновити існуючу конфігурацію типу цілі
     */
    GoalTypeResponse update(UUID id, GoalTypeRequest request);

    /**
     * Знайти тип цілі за ID (тільки якщо він належить користувачу)
     */
    GoalTypeResponse findById(UUID id);

    /**
     * Знайти всі типи цілей користувача з пагінацією
     */
    Page<GoalTypeResponse> findAll(Pageable pageable);

    /**
     * Видалити тип цілі
     */
    void delete(UUID id);
}
