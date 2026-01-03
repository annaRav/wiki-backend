package com.axis.goal.repository;

import com.axis.goal.model.entity.Goal;
import com.axis.goal.model.entity.GoalType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GoalTypeRepository extends JpaRepository<GoalType, UUID> {

    /**
     * Знайти всі типи цілей (рівні) для конкретного користувача
     */
    Page<GoalType> findByUserId(UUID userId, Pageable pageable);

    /**
     * Знайти конкретний тип цілі за ID та userId (для безпеки)
     */
    Optional<GoalType> findByIdAndUserId(UUID id, UUID userId);

    /**
     * Знайти конфігурацію рівня за його номером для конкретного користувача.
     * Оскільки на (user_id, level_number) стоїть UniqueConstraint, повертається Optional.
     */
    Optional<GoalType> findByUserIdAndLevelNumber(UUID userId, Integer levelNumber);

    /**
     * Видалити тип цілі за ID та userId
     */
    void deleteByIdAndUserId(UUID id, UUID userId);

    /**
     * Перевірити існування типу цілі для користувача
     */
    boolean existsByIdAndUserId(UUID id, UUID userId);

    /**
     * Перевірити, чи вже існує така назва типу для цього користувача
     */
    boolean existsByUserIdAndTitleIgnoreCase(UUID userId, String title);
}
