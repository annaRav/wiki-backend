package com.axis.goal.repository;

import com.axis.goal.model.entity.SubGoal;
import com.axis.goal.model.enums.ProgressStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class SubGoalRepository implements PanacheRepositoryBase<SubGoal, UUID> {

    public List<SubGoal> findByUserId(UUID userId, Page page, Sort sort) {
        return find("userId", sort, userId).page(page).list();
    }

    public long countByUserId(UUID userId) {
        return count("userId", userId);
    }

    public List<SubGoal> findByUserIdAndStatus(UUID userId, ProgressStatus status, Page page, Sort sort) {
        return find("userId = ?1 and status = ?2", sort, userId, status).page(page).list();
    }

    public long countByUserIdAndStatus(UUID userId, ProgressStatus status) {
        return count("userId = ?1 and status = ?2", userId, status);
    }

    public List<SubGoal> findByUserIdAndGoalId(UUID userId, UUID goalId, Page page, Sort sort) {
        return find("userId = ?1 and goal.id = ?2", sort, userId, goalId).page(page).list();
    }

    public long countByUserIdAndGoalId(UUID userId, UUID goalId) {
        return count("userId = ?1 and goal.id = ?2", userId, goalId);
    }

    public Optional<SubGoal> findByIdAndUserId(UUID id, UUID userId) {
        return find("id = ?1 and userId = ?2", id, userId).firstResultOptional();
    }

    public long deleteByIdAndUserId(UUID id, UUID userId) {
        return delete("id = ?1 and userId = ?2", id, userId);
    }

    public boolean existsByIdAndUserId(UUID id, UUID userId) {
        return count("id = ?1 and userId = ?2", id, userId) > 0;
    }
}
