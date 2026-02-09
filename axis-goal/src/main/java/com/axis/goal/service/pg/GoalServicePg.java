package com.axis.goal.service.pg;

import com.axis.common.exception.BusinessException;
import com.axis.common.exception.ResourceNotFoundException;
import com.axis.common.security.SecurityUtils;
import com.axis.goal.mapper.GoalMapper;
import com.axis.goal.model.dto.GoalRequest;
import com.axis.goal.model.dto.GoalResponse;
import com.axis.goal.model.dto.PageResponse;
import com.axis.goal.model.entity.CustomFieldDefinition;
import com.axis.goal.model.entity.Goal;
import com.axis.goal.model.entity.Goal.GoalStatus;
import com.axis.goal.model.entity.GoalType;
import com.axis.goal.repository.CustomFieldDefinitionRepository;
import com.axis.goal.repository.GoalRepository;
import com.axis.goal.repository.GoalTypeRepository;
import com.axis.goal.service.GoalService;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class GoalServicePg implements GoalService {

    @Inject
    GoalRepository goalRepository;

    @Inject
    GoalMapper goalMapper;

    @Inject
    CustomFieldDefinitionRepository fieldDefinitionRepository;

    @Inject
    GoalTypeRepository goalTypeRepository;

    @Inject
    SecurityUtils securityUtils;

    @Override
    @Transactional
    public GoalResponse create(GoalRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Creating new goal for user: {}", userId);

        // Fetch and validate the GoalType
        GoalType goalType = goalTypeRepository.findByIdAndUserId(request.typeId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("GoalType", request.typeId()));

        Goal goal = goalMapper.toEntity(request);
        goal.setUserId(userId);
        goal.setType(goalType);

        setupCustomFieldAnswers(goal);

        goalRepository.persist(goal);
        log.info("Created goal with id: {} for user: {}", goal.getId(), userId);

        return goalMapper.toResponse(goal);
    }

    @Override
    @Transactional
    public GoalResponse update(UUID id, GoalRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Updating goal: {} for user: {}", id, userId);

        Goal goal = goalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", id));

        // Fetch and validate the GoalType if it's being updated
        GoalType goalType = goalTypeRepository.findByIdAndUserId(request.typeId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("GoalType", request.typeId()));

        goalMapper.updateEntity(request, goal);
        goal.setType(goalType);
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
    public PageResponse<GoalResponse> findAll(int page, int size, String sortBy, String sortDirection) {
        UUID userId = getCurrentUserId();
        log.debug("Finding all goals for user: {}", userId);

        Sort sort = createSort(sortBy, sortDirection);
        List<Goal> goals = goalRepository.findByUserId(userId, Page.of(page, size), sort);
        long totalElements = goalRepository.countByUserId(userId);

        List<GoalResponse> responses = goals.stream()
                .map(goalMapper::toResponse)
                .toList();

        return PageResponse.of(responses, totalElements, page, size);
    }

    @Override
    public PageResponse<GoalResponse> findByStatus(GoalStatus status, int page, int size, String sortBy, String sortDirection) {
        UUID userId = getCurrentUserId();
        log.debug("Finding goals with status: {} for user: {}", status, userId);

        Sort sort = createSort(sortBy, sortDirection);
        List<Goal> goals = goalRepository.findByUserIdAndStatus(userId, status, Page.of(page, size), sort);
        long totalElements = goalRepository.countByUserIdAndStatus(userId, status);

        List<GoalResponse> responses = goals.stream()
                .map(goalMapper::toResponse)
                .toList();

        return PageResponse.of(responses, totalElements, page, size);
    }

    @Override
    public PageResponse<GoalResponse> findByTypeId(UUID typeId, int page, int size, String sortBy, String sortDirection) {
        UUID userId = getCurrentUserId();
        log.debug("Finding goals with type ID: {} for user: {}", typeId, userId);

        Sort sort = createSort(sortBy, sortDirection);
        List<Goal> goals = goalRepository.findByUserIdAndTypeId(userId, typeId, Page.of(page, size), sort);
        long totalElements = goalRepository.countByUserIdAndTypeId(userId, typeId);

        List<GoalResponse> responses = goals.stream()
                .map(goalMapper::toResponse)
                .toList();

        return PageResponse.of(responses, totalElements, page, size);
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
        return securityUtils.getCurrentUserIdAsUUID()
                .orElseThrow(() -> new IllegalStateException("User is not authenticated"));
    }

    private Sort createSort(String sortBy, String sortDirection) {
        if (sortBy == null || sortBy.isEmpty()) {
            sortBy = "createdAt";
        }
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection)
            ? Sort.Direction.Ascending
            : Sort.Direction.Descending;
        return Sort.by(sortBy, direction);
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
                CustomFieldDefinition definition = fieldDefinitionRepository.findByIdOptional(answer.getFieldDefinition().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("CustomFieldDefinition", answer.getFieldDefinition().getId()));

                if (!definition.getGoalType().getId().equals(goal.getType().getId())) {
                    throw new BusinessException(
                            "Custom field '" + definition.getLabel() + "' does not belong to goal type '" + goal.getType().getTitle() + "'",
                            Response.Status.BAD_REQUEST
                    );
                }

                answer.setFieldDefinition(definition);
            });
        }
    }
}
