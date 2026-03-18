package com.axis.goal.repository;

import com.axis.goal.model.entity.CustomFieldAnswer;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CustomFieldAnswerRepository implements PanacheRepositoryBase<CustomFieldAnswer, UUID> {

    public List<CustomFieldAnswer> findByOwnerId(UUID ownerId) {
        return find("ownerId", ownerId).list();
    }

    public Optional<CustomFieldAnswer> findByOwnerIdAndFieldDefinitionId(UUID ownerId, UUID fieldDefinitionId) {
        return find("ownerId = ?1 and fieldDefinition.id = ?2", ownerId, fieldDefinitionId).firstResultOptional();
    }

    public boolean existsByOwnerIdAndFieldDefinitionId(UUID ownerId, UUID fieldDefinitionId) {
        return count("ownerId = ?1 and fieldDefinition.id = ?2", ownerId, fieldDefinitionId) > 0;
    }

    public long deleteByOwnerId(UUID ownerId) {
        return delete("ownerId", ownerId);
    }
}
