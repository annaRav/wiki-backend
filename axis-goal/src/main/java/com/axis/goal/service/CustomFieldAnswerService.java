package com.axis.goal.service;

import com.axis.goal.model.dto.CustomFieldAnswerRequest;
import com.axis.goal.model.dto.CustomFieldAnswerResponse;

import java.util.List;
import java.util.UUID;

public interface CustomFieldAnswerService {

    /**
     * Create a new custom field answer for a goal
     */
    CustomFieldAnswerResponse create(UUID goalId, CustomFieldAnswerRequest request);

    /**
     * Update an existing custom field answer
     */
    CustomFieldAnswerResponse update(UUID id, CustomFieldAnswerRequest request);

    /**
     * Find a custom field answer by ID
     */
    CustomFieldAnswerResponse findById(UUID id);

    /**
     * Find all custom field answers for a specific goal
     */
    List<CustomFieldAnswerResponse> findByGoalId(UUID goalId);

    /**
     * Delete a custom field answer
     */
    void delete(UUID id);
}