package com.axis.goal.repository;

import com.axis.goal.model.entity.Goal;
import com.axis.goal.model.entity.GoalType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GoalTypeRepository extends JpaRepository<GoalType, UUID> {

    /**
     * Find all goal types (levels) for a specific user
     */
    Page<GoalType> findByUserId(UUID userId, Pageable pageable);

    /**
     * Find specific goal type by ID and userId (for security)
     */
    Optional<GoalType> findByIdAndUserId(UUID id, UUID userId);

    /**
     * Find level configuration by its number for a specific user.
     * Since there is a UniqueConstraint on (user_id, level_number), returns Optional.
     */
    Optional<GoalType> findByUserIdAndLevelNumber(UUID userId, Integer levelNumber);

    /**
     * Delete goal type by ID and userId
     */
    void deleteByIdAndUserId(UUID id, UUID userId);

    /**
     * Check if goal type exists for user
     */
    boolean existsByIdAndUserId(UUID id, UUID userId);

    /**
     * Check if such title already exists for this user
     */
    boolean existsByUserIdAndTitleIgnoreCase(UUID userId, String title);

    /**
     * Find maximum level number for user
     */
    @Query("SELECT COALESCE(MAX(gt.levelNumber), 0) FROM GoalType gt WHERE gt.userId = :userId")
    Integer findMaxLevelNumberByUserId(UUID userId);

    /**
     * Find all goal types with level greater than specified
     */
    List<GoalType> findByUserIdAndLevelNumberGreaterThanOrderByLevelNumber(UUID userId, Integer levelNumber);
}
