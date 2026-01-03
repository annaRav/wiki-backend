package com.axis.goal.service;

import com.axis.goal.model.dto.CustomFieldDefinitionRequest;
import com.axis.goal.model.dto.CustomFieldDefinitionResponse;

import java.util.List;
import java.util.UUID;

public interface CustomFieldDefinitionService {

    /**
     * Create a new custom field definition for a goal type
     */
    CustomFieldDefinitionResponse create(UUID goalTypeId, CustomFieldDefinitionRequest request);

    /**
     * Update an existing custom field definition
     */
    CustomFieldDefinitionResponse update(UUID id, CustomFieldDefinitionRequest request);

    /**
     * Find a custom field definition by ID
     */
    CustomFieldDefinitionResponse findById(UUID id);

    /**
     * Find all custom field definitions for a specific goal type
     */
    List<CustomFieldDefinitionResponse> findByGoalTypeId(UUID goalTypeId);

    /**
     * Delete a custom field definition
     */
    void delete(UUID id);

    /**
     * Check if a field key already exists for a goal type
     */
    boolean existsByGoalTypeIdAndKey(UUID goalTypeId, String key);
}