package com.axis.goal.service;

import com.axis.goal.model.dto.GoalRequest;
import com.axis.goal.model.dto.GoalResponse;
import com.axis.goal.model.dto.PageResponse;
import com.axis.goal.model.entity.Goal.GoalStatus;

import java.util.UUID;

public interface GoalService {

    /**
     * Create a new goal for the authenticated user
     */
    GoalResponse create(GoalRequest request);

    /**
     * Update an existing goal
     */
    GoalResponse update(UUID id, GoalRequest request);

    /**
     * Find a goal by ID (only returns if it belongs to the authenticated user)
     */
    GoalResponse findById(UUID id);

    /**
     * Find all goals for the authenticated user
     */
    PageResponse<GoalResponse> findAll(int page, int size, String sortBy, String sortDirection);

    /**
     * Find goals by status for the authenticated user
     */
    PageResponse<GoalResponse> findByStatus(GoalStatus status, int page, int size, String sortBy, String sortDirection);

    /**
     * Find goals by type ID for the authenticated user
     */
    PageResponse<GoalResponse> findByTypeId(UUID typeId, int page, int size, String sortBy, String sortDirection);

    /**
     * Delete a goal
     */
    void delete(UUID id);
}
