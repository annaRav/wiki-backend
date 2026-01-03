package com.axis.goal.repository;

import com.axis.goal.model.entity.CustomFieldAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomFieldAnswerRepository extends JpaRepository<CustomFieldAnswer, UUID> {

    /**
     * Find all answers for a specific goal.
     * Useful for loading all custom field values when displaying a goal.
     */
    List<CustomFieldAnswer> findByGoalId(UUID goalId);

    /**
     * Find a specific answer by goal and field definition.
     * Useful for checking if a required field has been filled.
     */
    Optional<CustomFieldAnswer> findByGoalIdAndFieldDefinitionId(UUID goalId, UUID fieldDefinitionId);

    /**
     * Find all answers for a specific field definition.
     * Useful for analytics or when migrating field definitions.
     */
    List<CustomFieldAnswer> findByFieldDefinitionId(UUID fieldDefinitionId);

    /**
     * Delete all answers for a specific goal.
     * Called when a goal is deleted (usually handled by cascade).
     */
    void deleteByGoalId(UUID goalId);

    /**
     * Check if an answer exists for a specific goal and field definition.
     */
    boolean existsByGoalIdAndFieldDefinitionId(UUID goalId, UUID fieldDefinitionId);
}