package com.axis.goal.repository;

import com.axis.goal.model.entity.Label;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class LabelRepository implements PanacheRepositoryBase<Label, UUID> {

    public List<Label> findByUserId(UUID userId) {
        return find("userId", userId).list();
    }

    public Optional<Label> findByIdAndUserId(UUID id, UUID userId) {
        return find("id = ?1 and userId = ?2", id, userId).firstResultOptional();
    }

    public List<Label> findByIdsAndUserId(List<UUID> ids, UUID userId) {
        return find("id in ?1 and userId = ?2", ids, userId).list();
    }

    public boolean existsByIdAndUserId(UUID id, UUID userId) {
        return count("id = ?1 and userId = ?2", id, userId) > 0;
    }

    public long deleteByIdAndUserId(UUID id, UUID userId) {
        return delete("id = ?1 and userId = ?2", id, userId);
    }
}