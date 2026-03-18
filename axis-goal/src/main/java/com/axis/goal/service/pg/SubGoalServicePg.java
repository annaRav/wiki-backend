package com.axis.goal.service.pg;

import com.axis.common.event.GoalDomainEvent;
import com.axis.common.event.GoalEventType;
import com.axis.common.exception.ResourceNotFoundException;
import com.axis.common.security.SecurityUtils;
import com.axis.goal.mapper.SubGoalMapper;
import com.axis.goal.messaging.GoalEventPublisher;
import com.axis.goal.model.dto.PageResponse;
import com.axis.goal.model.dto.SubGoalRequest;
import com.axis.goal.model.dto.SubGoalResponse;
import com.axis.goal.model.entity.Goal;
import com.axis.goal.model.entity.Label;
import com.axis.goal.model.entity.SubGoal;
import com.axis.goal.repository.GoalRepository;
import com.axis.goal.repository.LabelRepository;
import com.axis.goal.repository.SubGoalRepository;
import com.axis.goal.service.SubGoalService;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class SubGoalServicePg implements SubGoalService {

    @Inject
    SubGoalRepository subGoalRepository;

    @Inject
    GoalRepository goalRepository;

    @Inject
    SubGoalMapper subGoalMapper;

    @Inject
    LabelRepository labelRepository;

    @Inject
    SecurityUtils securityUtils;

    @Inject
    GoalEventPublisher goalEventPublisher;

    @Override
    @Transactional
    public SubGoalResponse create(SubGoalRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Creating sub-goal for user: {}", userId);

        Goal goal = goalRepository.findByIdAndUserId(request.goalId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", request.goalId()));

        SubGoal subGoal = subGoalMapper.toEntity(request);
        subGoal.setUserId(userId);
        subGoal.setGoal(goal);

        setupLabels(subGoal, request.labelIds(), userId);

        subGoalRepository.persist(subGoal);
        log.info("Created sub-goal with id: {} for user: {}", subGoal.getId(), userId);

        goalEventPublisher.publish(new GoalDomainEvent(
            UUID.randomUUID(), GoalEventType.SUBGOAL_CREATED, "SUB_GOAL",
            subGoal.getId(), subGoal.getGoal().getId(), subGoal.getUserId(),
            null, null, subGoal.getStatus().name(), subGoal.getTitle(), subGoal.getDescription(),
            null, Instant.now()
        ));

        return subGoalMapper.toResponse(subGoal);
    }

    @Override
    @Transactional
    public SubGoalResponse patch(UUID id, SubGoalRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Patching sub-goal: {} for user: {}", id, userId);

        SubGoal existing = subGoalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("SubGoal", id));

        String previousStatus = existing.getStatus() != null ? existing.getStatus().name() : null;

        subGoalMapper.patchEntity(request, existing);
        setupLabels(existing, request.labelIds(), userId);

        String newStatus = existing.getStatus() != null ? existing.getStatus().name() : null;
        if (!java.util.Objects.equals(previousStatus, newStatus)) {
            goalEventPublisher.publish(new GoalDomainEvent(
                UUID.randomUUID(), GoalEventType.SUBGOAL_STATUS_CHANGED, "SUB_GOAL",
                existing.getId(), existing.getGoal().getId(), existing.getUserId(),
                null, previousStatus, newStatus, existing.getTitle(), existing.getDescription(),
                null, Instant.now()
            ));
        } else {
            goalEventPublisher.publish(new GoalDomainEvent(
                UUID.randomUUID(), GoalEventType.SUBGOAL_UPDATED, "SUB_GOAL",
                existing.getId(), existing.getGoal().getId(), existing.getUserId(),
                null, null, newStatus, existing.getTitle(), existing.getDescription(),
                null, Instant.now()
            ));
        }

        log.info("Patched sub-goal: {} for user: {}", id, userId);
        return subGoalMapper.toResponse(existing);
    }

    @Override
    public SubGoalResponse findById(UUID id) {
        UUID userId = getCurrentUserId();
        log.debug("Finding sub-goal: {} for user: {}", id, userId);

        SubGoal subGoal = subGoalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("SubGoal", id));

        return subGoalMapper.toResponse(subGoal);
    }

    @Override
    public PageResponse<SubGoalResponse> findAll(int page, int size, String sortBy, String sortDirection) {
        UUID userId = getCurrentUserId();
        log.debug("Finding all sub-goals for user: {}", userId);

        Sort sort = createSort(sortBy, sortDirection);
        List<SubGoal> subGoals = subGoalRepository.findByUserId(userId, Page.of(page, size), sort);
        long totalElements = subGoalRepository.countByUserId(userId);

        List<SubGoalResponse> responses = subGoals.stream()
                .map(subGoalMapper::toResponse)
                .toList();

        return PageResponse.of(responses, totalElements, page, size);
    }

    @Override
    public PageResponse<SubGoalResponse> findByGoalId(UUID goalId, int page, int size, String sortBy, String sortDirection) {
        UUID userId = getCurrentUserId();
        log.debug("Finding sub-goals for goal: {} by user: {}", goalId, userId);

        Sort sort = createSort(sortBy, sortDirection);
        List<SubGoal> subGoals = subGoalRepository.findByUserIdAndGoalId(userId, goalId, Page.of(page, size), sort);
        long totalElements = subGoalRepository.countByUserIdAndGoalId(userId, goalId);

        List<SubGoalResponse> responses = subGoals.stream()
                .map(subGoalMapper::toResponse)
                .toList();

        return PageResponse.of(responses, totalElements, page, size);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        UUID userId = getCurrentUserId();
        log.debug("Deleting sub-goal: {} for user: {}", id, userId);

        SubGoal subGoal = subGoalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("SubGoal", id));

        UUID goalId = subGoal.getGoal().getId();

        subGoalRepository.deleteByIdAndUserId(id, userId);
        log.info("Deleted sub-goal: {} for user: {}", id, userId);

        goalEventPublisher.publish(new GoalDomainEvent(
            UUID.randomUUID(), GoalEventType.SUBGOAL_DELETED, "SUB_GOAL",
            id, goalId, userId, null, null, null, null, null, null, Instant.now()
        ));
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

    private void setupLabels(SubGoal subGoal, List<UUID> labelIds, UUID userId) {
        if (labelIds == null) {
            return;
        }
        if (labelIds.isEmpty()) {
            subGoal.getLabels().clear();
            return;
        }
        List<Label> labels = labelRepository.findByIdsAndUserId(labelIds, userId);
        subGoal.getLabels().clear();
        subGoal.getLabels().addAll(labels);
    }
}