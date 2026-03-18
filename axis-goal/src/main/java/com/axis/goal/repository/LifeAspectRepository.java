package com.axis.goal.repository;

import com.axis.goal.model.entity.LifeAspect;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class LifeAspectRepository implements PanacheRepositoryBase<LifeAspect, UUID> {

    public List<LifeAspect> findByUserId(UUID userId, Page page, Sort sort) {
        return find("userId", sort, userId).page(page).list();
    }

    public long countByUserId(UUID userId) {
        return count("userId", userId);
    }

    public Optional<LifeAspect> findByIdAndUserId(UUID id, UUID userId) {
        return find("id = ?1 and userId = ?2", id, userId).firstResultOptional();
    }

    public long deleteByIdAndUserId(UUID id, UUID userId) {
        return delete("id = ?1 and userId = ?2", id, userId);
    }

    public boolean existsByIdAndUserId(UUID id, UUID userId) {
        return count("id = ?1 and userId = ?2", id, userId) > 0;
    }
}
