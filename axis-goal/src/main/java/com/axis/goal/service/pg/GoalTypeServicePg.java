package com.axis.goal.service.pg;

import com.axis.common.exception.ResourceNotFoundException;
import com.axis.common.security.SecurityUtils;
import com.axis.goal.mapper.GoalTypeMapper;
import com.axis.goal.model.dto.GoalTypeRequest;
import com.axis.goal.model.dto.GoalTypeResponse;
import com.axis.goal.model.entity.GoalType;
import com.axis.goal.repository.GoalTypeRepository;
import com.axis.goal.service.GoalTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoalTypeServicePg implements GoalTypeService {

    private final GoalTypeRepository goalTypeRepository;
    private final GoalTypeMapper goalTypeMapper;

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

        GoalType saved = goalTypeRepository.save(goalType);
        log.info("Goal type created with ID: {} and level {} for user: {}",
                 saved.getId(), saved.getLevelNumber(), userId);

        return goalTypeMapper.toResponse(saved);
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
    public Page<GoalTypeResponse> findAll(Pageable pageable) {
        UUID userId = getCurrentUserId();
        log.debug("Getting all goal types for user: {}", userId);

        return goalTypeRepository.findByUserId(userId, pageable)
                .map(goalTypeMapper::toResponse);
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
            goalTypeRepository.saveAll(followingGoalTypes);
            log.info("Levels recalculated for {} goal types", followingGoalTypes.size());
        }
    }

    private UUID getCurrentUserId() {
        return SecurityUtils.getCurrentUserIdAsUUID()
                .orElseThrow(() -> new IllegalStateException("User not authorized"));
    }
}
