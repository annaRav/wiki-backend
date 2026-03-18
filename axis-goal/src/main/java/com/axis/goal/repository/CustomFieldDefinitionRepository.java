package com.axis.goal.repository;

import com.axis.goal.model.entity.CustomFieldDefinition;
import com.axis.goal.model.enums.OwnerType;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CustomFieldDefinitionRepository implements PanacheRepositoryBase<CustomFieldDefinition, UUID> {

    public List<CustomFieldDefinition> findByOwnerTypeAndUserId(OwnerType ownerType, UUID userId) {
        return find("ownerType = ?1 and userId = ?2", ownerType, userId).list();
    }

    public List<CustomFieldDefinition> findRequiredByOwnerTypeAndUserId(OwnerType ownerType, UUID userId) {
        return find("ownerType = ?1 and userId = ?2 and required = true", ownerType, userId).list();
    }
}
