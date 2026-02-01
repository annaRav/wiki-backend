package com.axis.goal.repository;

import com.axis.goal.model.entity.Goal;
import com.axis.goal.model.entity.Goal.GoalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GoalRepository extends JpaRepository<Goal, UUID> {

    /**
     * Find all goals for a specific user
     */
    Page<Goal> findByUserId(UUID userId, Pageable pageable);

    /**
     * Find goals by user and status
     */
    Page<Goal> findByUserIdAndStatus(UUID userId, GoalStatus status, Pageable pageable);

    /**
     * Find goals by user and type ID
     */
    Page<Goal> findByUserIdAndTypeId(UUID userId, UUID typeId, Pageable pageable);

    /**
     * Find a specific goal by id and userId (for security)
     */
    Optional<Goal> findByIdAndUserId(UUID id, UUID userId);

    /**
     * Delete a goal by id and userId (for security)
     */
    void deleteByIdAndUserId(UUID id, UUID userId);

    /**
     * Check if a goal exists for a user
     */
    boolean existsByIdAndUserId(UUID id, UUID userId);
}