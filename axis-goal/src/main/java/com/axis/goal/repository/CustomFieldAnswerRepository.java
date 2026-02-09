package com.axis.goal.repository;

import com.axis.goal.model.entity.CustomFieldAnswer;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CustomFieldAnswerRepository implements PanacheRepositoryBase<CustomFieldAnswer, UUID> {

    /**
     * Find all answers for a specific goal.
     * Useful for loading all custom field values when displaying a goal.
     */
    public List<CustomFieldAnswer> findByGoalId(UUID goalId) {
        return find("goal.id", goalId).list();
    }

    /**
     * Find a specific answer by goal and field definition.
     * Useful for checking if a required field has been filled.
     */
    public Optional<CustomFieldAnswer> findByGoalIdAndFieldDefinitionId(UUID goalId, UUID fieldDefinitionId) {
        return find("goal.id = ?1 and fieldDefinition.id = ?2", goalId, fieldDefinitionId).firstResultOptional();
    }

    /**
     * Find all answers for a specific field definition.
     * Useful for analytics or when migrating field definitions.
     */
    public List<CustomFieldAnswer> findByFieldDefinitionId(UUID fieldDefinitionId) {
        return find("fieldDefinition.id", fieldDefinitionId).list();
    }

    /**
     * Delete all answers for a specific goal.
     * Called when a goal is deleted (usually handled by cascade).
     */
    public long deleteByGoalId(UUID goalId) {
        return delete("goal.id", goalId);
    }

    /**
     * Check if an answer exists for a specific goal and field definition.
     */
    public boolean existsByGoalIdAndFieldDefinitionId(UUID goalId, UUID fieldDefinitionId) {
        return count("goal.id = ?1 and fieldDefinition.id = ?2", goalId, fieldDefinitionId) > 0;
    }
}
