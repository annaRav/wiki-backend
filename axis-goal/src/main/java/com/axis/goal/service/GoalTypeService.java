package com.axis.goal.service;

import com.axis.goal.model.dto.GoalTypeRequest;
import com.axis.goal.model.dto.GoalTypeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GoalTypeService {

    /**
     * Create a new goal type for the authenticated user
     */
    GoalTypeResponse create(GoalTypeRequest request);

    /**
     * Update an existing goal type configuration
     */
    GoalTypeResponse update(UUID id, GoalTypeRequest request);

    /**
     * Find goal type by ID (only if it belongs to the user)
     */
    GoalTypeResponse findById(UUID id);

    /**
     * Find all user's goal types with pagination
     */
    Page<GoalTypeResponse> findAll(Pageable pageable);

    /**
     * Delete a goal type
     */
    void delete(UUID id);
}
