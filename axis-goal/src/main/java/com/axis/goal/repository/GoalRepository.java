package com.axis.goal.repository;

import com.axis.goal.model.entity.Goal;
import com.axis.goal.model.enums.ProgressStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class GoalRepository implements PanacheRepositoryBase<Goal, UUID> {

    public List<Goal> findByUserId(UUID userId, Page page, Sort sort) {
        return find("userId", sort, userId).page(page).list();
    }

    public long countByUserId(UUID userId) {
        return count("userId", userId);
    }

    public List<Goal> findByUserIdAndStatus(UUID userId, ProgressStatus status, Page page, Sort sort) {
        return find("userId = ?1 and status = ?2", sort, userId, status).page(page).list();
    }

    public long countByUserIdAndStatus(UUID userId, ProgressStatus status) {
        return count("userId = ?1 and status = ?2", userId, status);
    }

    public List<Goal> findByUserIdAndLifeAspectId(UUID userId, UUID lifeAspectId, Page page, Sort sort) {
        return find("userId = ?1 and lifeAspect.id = ?2", sort, userId, lifeAspectId).page(page).list();
    }

    public long countByUserIdAndLifeAspectId(UUID userId, UUID lifeAspectId) {
        return count("userId = ?1 and lifeAspect.id = ?2", userId, lifeAspectId);
    }

    public Optional<Goal> findByIdAndUserId(UUID id, UUID userId) {
        return find("id = ?1 and userId = ?2", id, userId).firstResultOptional();
    }

    public long deleteByIdAndUserId(UUID id, UUID userId) {
        return delete("id = ?1 and userId = ?2", id, userId);
    }

    public boolean existsByIdAndUserId(UUID id, UUID userId) {
        return count("id = ?1 and userId = ?2", id, userId) > 0;
    }
}
