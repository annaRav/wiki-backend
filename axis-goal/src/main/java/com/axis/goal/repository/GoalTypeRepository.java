package com.axis.goal.repository;

import com.axis.goal.model.entity.GoalType;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class GoalTypeRepository implements PanacheRepositoryBase<GoalType, UUID> {

    /**
     * Find all goal types (levels) for a specific user
     */
    public List<GoalType> findByUserId(UUID userId, Page page, Sort sort) {
        return find("userId", sort, userId)
            .page(page)
            .list();
    }

    /**
     * Count goal types for a user
     */
    public long countByUserId(UUID userId) {
        return count("userId", userId);
    }

    /**
     * Find specific goal type by ID and userId (for security)
     */
    public Optional<GoalType> findByIdAndUserId(UUID id, UUID userId) {
        return find("id = ?1 and userId = ?2", id, userId).firstResultOptional();
    }

    /**
     * Find level configuration by its number for a specific user.
     * Since there is a UniqueConstraint on (user_id, level_number), returns Optional.
     */
    public Optional<GoalType> findByUserIdAndLevelNumber(UUID userId, Integer levelNumber) {
        return find("userId = ?1 and levelNumber = ?2", userId, levelNumber).firstResultOptional();
    }

    /**
     * Delete goal type by ID and userId
     */
    public long deleteByIdAndUserId(UUID id, UUID userId) {
        return delete("id = ?1 and userId = ?2", id, userId);
    }

    /**
     * Check if goal type exists for user
     */
    public boolean existsByIdAndUserId(UUID id, UUID userId) {
        return count("id = ?1 and userId = ?2", id, userId) > 0;
    }

    /**
     * Check if such title already exists for this user
     */
    public boolean existsByUserIdAndTitleIgnoreCase(UUID userId, String title) {
        return count("userId = ?1 and lower(title) = lower(?2)", userId, title) > 0;
    }

    /**
     * Find maximum level number for user
     */
    public Integer findMaxLevelNumberByUserId(UUID userId) {
        List<GoalType> types = find("userId = ?1", Sort.descending("levelNumber"), userId).list();
        return types.isEmpty() ? 0 : types.get(0).getLevelNumber();
    }

    /**
     * Find all goal types with level greater than specified
     */
    public List<GoalType> findByUserIdAndLevelNumberGreaterThanOrderByLevelNumber(UUID userId, Integer levelNumber) {
        return find("userId = ?1 and levelNumber > ?2", Sort.by("levelNumber"), userId, levelNumber).list();
    }
}
