package com.axis.goal.repository;

import com.axis.goal.model.entity.Goal;
import com.axis.goal.model.entity.Goal.GoalStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class GoalRepository implements PanacheRepositoryBase<Goal, UUID> {

    /**
     * Find all goals for a specific user
     */
    public List<Goal> findByUserId(UUID userId, Page page, Sort sort) {
        return find("userId", sort, userId)
            .page(page)
            .list();
    }

    /**
     * Count all goals for a specific user
     */
    public long countByUserId(UUID userId) {
        return count("userId", userId);
    }

    /**
     * Find goals by user and status
     */
    public List<Goal> findByUserIdAndStatus(UUID userId, GoalStatus status, Page page, Sort sort) {
        return find("userId = ?1 and status = ?2", sort, userId, status)
            .page(page)
            .list();
    }

    /**
     * Count goals by user and status
     */
    public long countByUserIdAndStatus(UUID userId, GoalStatus status) {
        return count("userId = ?1 and status = ?2", userId, status);
    }

    /**
     * Find goals by user and type ID
     */
    public List<Goal> findByUserIdAndTypeId(UUID userId, UUID typeId, Page page, Sort sort) {
        return find("userId = ?1 and type.id = ?2", sort, userId, typeId)
            .page(page)
            .list();
    }

    /**
     * Count goals by user and type ID
     */
    public long countByUserIdAndTypeId(UUID userId, UUID typeId) {
        return count("userId = ?1 and type.id = ?2", userId, typeId);
    }

    /**
     * Find a specific goal by id and userId (for security)
     */
    public Optional<Goal> findByIdAndUserId(UUID id, UUID userId) {
        return find("id = ?1 and userId = ?2", id, userId).firstResultOptional();
    }

    /**
     * Delete a goal by id and userId (for security)
     */
    public long deleteByIdAndUserId(UUID id, UUID userId) {
        return delete("id = ?1 and userId = ?2", id, userId);
    }

    /**
     * Check if a goal exists for a user
     */
    public boolean existsByIdAndUserId(UUID id, UUID userId) {
        return count("id = ?1 and userId = ?2", id, userId) > 0;
    }
}
