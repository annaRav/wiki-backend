package com.axis.goal.service.pg;

import com.axis.common.event.GoalDomainEvent;
import com.axis.common.event.GoalEventType;
import com.axis.common.exception.ResourceNotFoundException;
import com.axis.common.security.SecurityUtils;
import com.axis.goal.mapper.GoalMapper;
import com.axis.goal.messaging.GoalEventPublisher;
import com.axis.goal.model.dto.GoalRequest;
import com.axis.goal.model.dto.GoalResponse;
import com.axis.goal.model.dto.PageResponse;
import com.axis.goal.model.entity.Goal;
import com.axis.goal.model.entity.Label;
import com.axis.goal.model.entity.LifeAspect;
import com.axis.goal.model.enums.ProgressStatus;
import com.axis.goal.repository.GoalRepository;
import com.axis.goal.repository.LifeAspectRepository;
import com.axis.goal.repository.LabelRepository;
import com.axis.goal.service.GoalService;
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
public class GoalServicePg implements GoalService {

    @Inject
    GoalRepository goalRepository;

    @Inject
    GoalMapper goalMapper;

    @Inject
    LifeAspectRepository lifeAspectRepository;

    @Inject
    LabelRepository labelRepository;

    @Inject
    SecurityUtils securityUtils;

    @Inject
    GoalEventPublisher goalEventPublisher;

    @Override
    @Transactional
    public GoalResponse create(GoalRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Creating new goal for user: {}", userId);

        LifeAspect lifeAspect = lifeAspectRepository.findByIdAndUserId(request.lifeAspectId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("LifeAspect", request.lifeAspectId()));

        Goal goal = goalMapper.toEntity(request);
        goal.setUserId(userId);
        goal.setLifeAspect(lifeAspect);

        setupLabels(goal, request.labelIds(), userId);

        goalRepository.persist(goal);
        log.info("Created goal with id: {} for user: {}", goal.getId(), userId);

        goalEventPublisher.publish(new GoalDomainEvent(
            UUID.randomUUID(), GoalEventType.GOAL_CREATED, "GOAL",
            goal.getId(), goal.getId(), goal.getUserId(),
            goal.getLifeAspect() != null ? goal.getLifeAspect().getId().toString() : null,
            null, goal.getStatus().name(), goal.getTitle(), goal.getDescription(),
            null, Instant.now()
        ));

        return goalMapper.toResponse(goal);
    }

    @Override
    @Transactional
    public GoalResponse patch(UUID id, GoalRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Patching goal: {} for user: {}", id, userId);

        Goal existingGoal = goalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", id));

        String previousStatus = existingGoal.getStatus() != null ? existingGoal.getStatus().name() : null;

        goalMapper.patchEntity(request, existingGoal);
        setupLabels(existingGoal, request.labelIds(), userId);

        String newStatus = existingGoal.getStatus() != null ? existingGoal.getStatus().name() : null;
        if (!java.util.Objects.equals(previousStatus, newStatus)) {
            goalEventPublisher.publish(new GoalDomainEvent(
                UUID.randomUUID(), GoalEventType.GOAL_STATUS_CHANGED, "GOAL",
                existingGoal.getId(), existingGoal.getId(), existingGoal.getUserId(),
                existingGoal.getLifeAspect() != null ? existingGoal.getLifeAspect().getId().toString() : null,
                previousStatus, newStatus, existingGoal.getTitle(), existingGoal.getDescription(),
                null, Instant.now()
            ));
        } else {
            goalEventPublisher.publish(new GoalDomainEvent(
                UUID.randomUUID(), GoalEventType.GOAL_UPDATED, "GOAL",
                existingGoal.getId(), existingGoal.getId(), existingGoal.getUserId(),
                existingGoal.getLifeAspect() != null ? existingGoal.getLifeAspect().getId().toString() : null,
                null, newStatus, existingGoal.getTitle(), existingGoal.getDescription(),
                null, Instant.now()
            ));
        }

        log.info("Goal patched: {} for user: {}", id, userId);
        return goalMapper.toResponse(existingGoal);
    }

    @Override
    public GoalResponse findById(UUID id) {
        UUID userId = getCurrentUserId();
        Goal goal = goalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", id));
        return goalMapper.toResponse(goal);
    }

    @Override
    public PageResponse<GoalResponse> findAll(int page, int size, String sortBy, String sortDirection) {
        UUID userId = getCurrentUserId();
        Sort sort = createSort(sortBy, sortDirection);
        List<Goal> goals = goalRepository.findByUserId(userId, Page.of(page, size), sort);
        long total = goalRepository.countByUserId(userId);
        return PageResponse.of(goals.stream().map(goalMapper::toResponse).toList(), total, page, size);
    }

    @Override
    public PageResponse<GoalResponse> findByStatus(ProgressStatus status, int page, int size, String sortBy, String sortDirection) {
        UUID userId = getCurrentUserId();
        Sort sort = createSort(sortBy, sortDirection);
        List<Goal> goals = goalRepository.findByUserIdAndStatus(userId, status, Page.of(page, size), sort);
        long total = goalRepository.countByUserIdAndStatus(userId, status);
        return PageResponse.of(goals.stream().map(goalMapper::toResponse).toList(), total, page, size);
    }

    @Override
    public PageResponse<GoalResponse> findByLifeAspectId(UUID lifeAspectId, int page, int size, String sortBy, String sortDirection) {
        UUID userId = getCurrentUserId();
        Sort sort = createSort(sortBy, sortDirection);
        List<Goal> goals = goalRepository.findByUserIdAndLifeAspectId(userId, lifeAspectId, Page.of(page, size), sort);
        long total = goalRepository.countByUserIdAndLifeAspectId(userId, lifeAspectId);
        return PageResponse.of(goals.stream().map(goalMapper::toResponse).toList(), total, page, size);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        UUID userId = getCurrentUserId();
        if (!goalRepository.existsByIdAndUserId(id, userId)) {
            throw new ResourceNotFoundException("Goal", id);
        }
        goalRepository.deleteByIdAndUserId(id, userId);
        log.info("Deleted goal: {} for user: {}", id, userId);

        goalEventPublisher.publish(new GoalDomainEvent(
            UUID.randomUUID(), GoalEventType.GOAL_DELETED, "GOAL",
            id, id, userId, null, null, null, null, null, null, Instant.now()
        ));
    }

    private UUID getCurrentUserId() {
        return securityUtils.getCurrentUserIdAsUUID()
                .orElseThrow(() -> new IllegalStateException("User is not authenticated"));
    }

    private Sort createSort(String sortBy, String sortDirection) {
        if (sortBy == null || sortBy.isEmpty()) sortBy = "createdAt";
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.Ascending : Sort.Direction.Descending;
        return Sort.by(sortBy, direction);
    }

    private void setupLabels(Goal goal, List<UUID> labelIds, UUID userId) {
        if (labelIds == null) return;
        if (labelIds.isEmpty()) { goal.getLabels().clear(); return; }
        List<Label> labels = labelRepository.findByIdsAndUserId(labelIds, userId);
        goal.getLabels().clear();
        goal.getLabels().addAll(labels);
    }
}