package com.axis.goal.repository;

import com.axis.goal.model.entity.CustomFieldDefinition;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CustomFieldDefinitionRepository implements PanacheRepositoryBase<CustomFieldDefinition, UUID> {

    /**
     * Find all field definitions associated with a specific goal type.
     * Useful for building dynamic forms on the frontend.
     */
    public List<CustomFieldDefinition> findByGoalTypeId(UUID goalTypeId) {
        return find("goalType.id", goalTypeId).list();
    }

    /**
     * Find all required fields for a specific goal type.
     * Useful for server-side validation during goal creation.
     */
    public List<CustomFieldDefinition> findByGoalTypeIdAndRequiredTrue(UUID goalTypeId) {
        return find("goalType.id = ?1 and required = true", goalTypeId).list();
    }
}
