package com.axis.goal.service.pg;

import com.axis.common.exception.BusinessException;
import com.axis.common.exception.ResourceNotFoundException;
import com.axis.common.security.SecurityUtils;
import com.axis.goal.mapper.GoalMapper;
import com.axis.goal.model.dto.GoalRequest;
import com.axis.goal.model.dto.GoalResponse;
import com.axis.goal.model.entity.CustomFieldDefinition;
import com.axis.goal.model.entity.Goal;
import com.axis.goal.model.entity.Goal.GoalStatus;
import com.axis.goal.model.entity.GoalType;
import com.axis.goal.repository.CustomFieldDefinitionRepository;
import com.axis.goal.repository.GoalRepository;
import com.axis.goal.service.GoalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoalServicePg implements GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final CustomFieldDefinitionRepository fieldDefinitionRepository;

    @Override
    @Transactional
    public GoalResponse create(GoalRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Creating new goal for user: {}", userId);

        Goal goal = goalMapper.toEntity(request);
        goal.setUserId(userId);

        setupCustomFieldAnswers(goal);

        Goal saved = goalRepository.save(goal);
        log.info("Created goal with id: {} for user: {}", saved.getId(), userId);

        return goalMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public GoalResponse update(UUID id, GoalRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Updating goal: {} for user: {}", id, userId);

        Goal goal = goalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", id));

        goalMapper.updateEntity(request, goal);
        setupCustomFieldAnswers(goal);

        log.info("Updated goal: {} for user: {}", id, userId);
        return goalMapper.toResponse(goal);
    }

    @Override
    public GoalResponse findById(UUID id) {
        UUID userId = getCurrentUserId();
        log.debug("Finding goal: {} for user: {}", id, userId);

        Goal goal = goalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", id));

        return goalMapper.toResponse(goal);
    }

    @Override
    public Page<GoalResponse> findAll(Pageable pageable) {
        UUID userId = getCurrentUserId();
        log.debug("Finding all goals for user: {}", userId);

        return goalRepository.findByUserId(userId, pageable)
                .map(goalMapper::toResponse);
    }

    @Override
    public Page<GoalResponse> findByStatus(GoalStatus status, Pageable pageable) {
        UUID userId = getCurrentUserId();
        log.debug("Finding goals with status: {} for user: {}", status, userId);

        return goalRepository.findByUserIdAndStatus(userId, status, pageable)
                .map(goalMapper::toResponse);
    }

    @Override
    public Page<GoalResponse> findByType(GoalType type, Pageable pageable) {
        UUID userId = getCurrentUserId();
        log.debug("Finding goals with type: {} for user: {}", type, userId);

        return goalRepository.findByUserIdAndType(userId, type, pageable)
                .map(goalMapper::toResponse);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        UUID userId = getCurrentUserId();
        log.debug("Deleting goal: {} for user: {}", id, userId);

        if (!goalRepository.existsByIdAndUserId(id, userId)) {
            throw new ResourceNotFoundException("Goal", id);
        }

        goalRepository.deleteByIdAndUserId(id, userId);
        log.info("Deleted goal: {} for user: {}", id, userId);
    }

    private UUID getCurrentUserId() {
        return SecurityUtils.getCurrentUserIdAsUUID()
                .orElseThrow(() -> new IllegalStateException("User is not authenticated"));
    }

    /**
     * Sets up bidirectional relationships for custom field answers and validates them.
     * Similar to GoalTypeServicePg handling custom field definitions.
     */
    private void setupCustomFieldAnswers(Goal goal) {
        if (goal.getCustomAnswers() != null && !goal.getCustomAnswers().isEmpty()) {
            goal.getCustomAnswers().forEach(answer -> {
                answer.setGoal(goal);

                // Validate that field definition exists and belongs to the goal's type
                CustomFieldDefinition definition = fieldDefinitionRepository.findById(answer.getFieldDefinition().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("CustomFieldDefinition", answer.getFieldDefinition().getId()));

                if (!definition.getGoalType().getId().equals(goal.getType().getId())) {
                    throw new BusinessException(
                            "Custom field '" + definition.getLabel() + "' does not belong to goal type '" + goal.getType().getTitle() + "'",
                            HttpStatus.BAD_REQUEST
                    );
                }

                answer.setFieldDefinition(definition);
            });
        }
    }
}