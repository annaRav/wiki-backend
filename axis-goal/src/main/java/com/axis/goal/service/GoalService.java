package com.axis.goal.service;

import com.axis.goal.model.dto.GoalRequest;
import com.axis.goal.model.dto.GoalResponse;
import com.axis.goal.model.entity.Goal.GoalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    Page<GoalResponse> findAll(Pageable pageable);

    /**
     * Find goals by status for the authenticated user
     */
    Page<GoalResponse> findByStatus(GoalStatus status, Pageable pageable);

    /**
     * Find goals by type ID for the authenticated user
     */
    Page<GoalResponse> findByTypeId(UUID typeId, Pageable pageable);

    /**
     * Delete a goal
     */
    void delete(UUID id);
}