package com.axis.goal.service.pg;

import com.axis.common.exception.ResourceNotFoundException;
import com.axis.common.security.SecurityUtils;
import com.axis.goal.mapper.GoalTypeMapper;
import com.axis.goal.model.dto.GoalTypeRequest;
import com.axis.goal.model.dto.GoalTypeResponse;
import com.axis.goal.model.dto.PageResponse;
import com.axis.goal.model.entity.GoalType;
import com.axis.goal.repository.GoalTypeRepository;
import com.axis.goal.service.GoalTypeService;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class GoalTypeServicePg implements GoalTypeService {

    @Inject
    GoalTypeRepository goalTypeRepository;

    @Inject
    GoalTypeMapper goalTypeMapper;

    @Inject
    SecurityUtils securityUtils;

    @Override
    @Transactional
    public GoalTypeResponse create(GoalTypeRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Creating new goal type '{}' for user: {}", request.title(), userId);

        GoalType goalType = goalTypeMapper.toEntity(request);
        goalType.setUserId(userId);

        // Automatically calculate levelNumber: max level + 1
        Integer maxLevel = goalTypeRepository.findMaxLevelNumberByUserId(userId);
        goalType.setLevelNumber(maxLevel + 1);
        log.debug("New goal type will receive level: {}", goalType.getLevelNumber());

        if (goalType.getCustomFields() != null) {
            goalType.getCustomFields().forEach(field -> field.setGoalType(goalType));
        }

        goalTypeRepository.persist(goalType);
        log.info("Goal type created with ID: {} and level {} for user: {}",
                 goalType.getId(), goalType.getLevelNumber(), userId);

        return goalTypeMapper.toResponse(goalType);
    }

    @Override
    @Transactional
    public GoalTypeResponse update(UUID id, GoalTypeRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Updating goal type: {} for user: {}", id, userId);

        GoalType existingType = goalTypeRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("GoalType", id));

        goalTypeMapper.updateEntity(request, existingType);

        existingType.setUserId(userId);
        if (existingType.getCustomFields() != null) {
            existingType.getCustomFields().forEach(field -> field.setGoalType(existingType));
        }

        log.info("Goal type updated: {} for user: {}", id, userId);
        return goalTypeMapper.toResponse(existingType);
    }

    @Override
    public GoalTypeResponse findById(UUID id) {
        UUID userId = getCurrentUserId();
        log.debug("Finding goal type: {} for user: {}", id, userId);

        return goalTypeRepository.findByIdAndUserId(id, userId)
                .map(goalTypeMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("GoalType", id));
    }

    @Override
    public PageResponse<GoalTypeResponse> findAll(int page, int size, String sortBy, String sortDirection) {
        UUID userId = getCurrentUserId();
        log.debug("Getting all goal types for user: {}", userId);

        Sort sort = createSort(sortBy, sortDirection);
        List<GoalType> goalTypes = goalTypeRepository.findByUserId(userId, Page.of(page, size), sort);
        long totalElements = goalTypeRepository.countByUserId(userId);

        List<GoalTypeResponse> responses = goalTypes.stream()
                .map(goalTypeMapper::toResponse)
                .toList();

        return PageResponse.of(responses, totalElements, page, size);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        UUID userId = getCurrentUserId();
        log.debug("Deleting goal type: {} for user: {}", id, userId);

        // Find goal type before deletion to get its level
        GoalType goalType = goalTypeRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("GoalType", id));

        Integer deletedLevel = goalType.getLevelNumber();
        log.debug("Deleting goal type with level: {}", deletedLevel);

        // Delete goal type
        goalTypeRepository.deleteByIdAndUserId(id, userId);
        log.info("Goal type deleted: {} with related data for user: {}", id, userId);

        // Recalculate levels for all following goal types (decrement by 1)
        var followingGoalTypes = goalTypeRepository
                .findByUserIdAndLevelNumberGreaterThanOrderByLevelNumber(userId, deletedLevel);

        if (!followingGoalTypes.isEmpty()) {
            log.debug("Recalculating levels for {} following goal types", followingGoalTypes.size());
            followingGoalTypes.forEach(gt -> gt.setLevelNumber(gt.getLevelNumber() - 1));
            // Panache doesn't have saveAll, entities are automatically persisted in transaction
            log.info("Levels recalculated for {} goal types", followingGoalTypes.size());
        }
    }

    private UUID getCurrentUserId() {
        return securityUtils.getCurrentUserIdAsUUID()
                .orElseThrow(() -> new IllegalStateException("User not authorized"));
    }

    private Sort createSort(String sortBy, String sortDirection) {
        if (sortBy == null || sortBy.isEmpty()) {
            sortBy = "levelNumber";
        }
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection)
            ? Sort.Direction.Ascending
            : Sort.Direction.Descending;
        return Sort.by(sortBy, direction);
    }
}
