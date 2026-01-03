package com.axis.goal.repository;

import com.axis.goal.model.entity.CustomFieldDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomFieldDefinitionRepository extends JpaRepository<CustomFieldDefinition, UUID> {

    /**
     * Find all field definitions associated with a specific goal type.
     * Useful for building dynamic forms on the frontend.
     */
    List<CustomFieldDefinition> findByGoalTypeId(UUID goalTypeId);

    /**
     * Find a field definition by its technical key and goal type ID.
     * Keys must be unique within a single GoalType.
     */
    Optional<CustomFieldDefinition> findByGoalTypeIdAndKey(UUID goalTypeId, String key);

    /**
     * Check if a field definition with the given key exists in a specific goal type.
     */
    boolean existsByGoalTypeIdAndKey(UUID goalTypeId, String key);

    /**
     * Find all required fields for a specific goal type.
     * Useful for server-side validation during goal creation.
     */
    List<CustomFieldDefinition> findByGoalTypeIdAndRequiredTrue(UUID goalTypeId);
}
