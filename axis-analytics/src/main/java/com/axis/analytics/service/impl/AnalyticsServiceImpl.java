package com.axis.analytics.service.impl;

import com.axis.analytics.model.document.GoalEventDocument;
import com.axis.analytics.model.document.GoalSnapshot;
import com.axis.analytics.model.document.StatusPeriod;
import com.axis.analytics.model.document.SubGoalSummary;
import com.axis.analytics.repository.GoalEventRepository;
import com.axis.analytics.repository.GoalSnapshotRepository;
import com.axis.analytics.service.AnalyticsService;
import com.axis.common.event.GoalDomainEvent;
import com.axis.common.exception.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class AnalyticsServiceImpl implements AnalyticsService {

    @Inject
    GoalEventRepository eventRepository;

    @Inject
    GoalSnapshotRepository snapshotRepository;

    @Override
    public void processEvent(GoalDomainEvent event) {
        // Save raw event
        GoalEventDocument doc = GoalEventDocument.builder()
            .eventId(event.eventId().toString())
            .eventType(event.eventType().name())
            .entityType(event.entityType())
            .entityId(event.entityId().toString())
            .goalId(event.goalId().toString())
            .userId(event.userId().toString())
            .previousStatus(event.previousStatus())
            .newStatus(event.newStatus())
            .title(event.title())
            .description(event.description())
            .lifeAspectId(event.lifeAspectId())
            .changes(event.changes())
            .occurredAt(event.occurredAt())
            .build();
        eventRepository.persist(doc);

        // Update snapshot
        updateSnapshot(event);
        log.debug("Processed event {} for goal {}", event.eventType(), event.goalId());
    }

    private void updateSnapshot(GoalDomainEvent event) {
        String goalId = event.goalId().toString();

        switch (event.eventType()) {
            case GOAL_CREATED -> {
                GoalSnapshot snapshot = GoalSnapshot.builder()
                    .goalId(goalId)
                    .userId(event.userId().toString())
                    .title(event.title())
                    .lifeAspectId(event.lifeAspectId())
                    .currentStatus(event.newStatus())
                    .createdAt(event.occurredAt())
                    .statusHistory(new ArrayList<>(List.of(
                        StatusPeriod.builder()
                            .status(event.newStatus())
                            .enteredAt(event.occurredAt())
                            .build()
                    )))
                    .subGoals(new ArrayList<>())
                    .build();
                snapshotRepository.persist(snapshot);
            }
            case GOAL_STATUS_CHANGED -> {
                snapshotRepository.findByGoalId(goalId).ifPresent(snapshot -> {
                    // Close previous status period
                    if (!snapshot.getStatusHistory().isEmpty()) {
                        StatusPeriod last = snapshot.getStatusHistory().getLast();
                        if (last.getExitedAt() == null) {
                            last.setExitedAt(event.occurredAt());
                            last.setDurationMs(event.occurredAt().toEpochMilli() - last.getEnteredAt().toEpochMilli());
                        }
                    }
                    // Open new status period
                    snapshot.getStatusHistory().add(StatusPeriod.builder()
                        .status(event.newStatus())
                        .enteredAt(event.occurredAt())
                        .build());
                    snapshot.setCurrentStatus(event.newStatus());
                    if ("COMPLETED".equals(event.newStatus())) {
                        snapshot.setCompletedAt(event.occurredAt());
                    }
                    snapshotRepository.update(snapshot);
                });
            }
            case GOAL_UPDATED -> {
                snapshotRepository.findByGoalId(goalId).ifPresent(snapshot -> {
                    if (event.title() != null) snapshot.setTitle(event.title());
                    if (event.lifeAspectId() != null) snapshot.setLifeAspectId(event.lifeAspectId());
                    snapshotRepository.update(snapshot);
                });
            }
            case GOAL_DELETED -> {
                snapshotRepository.findByGoalId(goalId).ifPresent(snapshotRepository::delete);
            }
            case SUBGOAL_CREATED -> {
                snapshotRepository.findByGoalId(goalId).ifPresent(snapshot -> {
                    snapshot.getSubGoals().add(SubGoalSummary.builder()
                        .subGoalId(event.entityId().toString())
                        .title(event.title())
                        .currentStatus(event.newStatus())
                        .createdAt(event.occurredAt())
                        .statusHistory(new ArrayList<>(List.of(
                            StatusPeriod.builder()
                                .status(event.newStatus())
                                .enteredAt(event.occurredAt())
                                .build()
                        )))
                        .build());
                    snapshot.setTotalSubGoals(snapshot.getSubGoals().size());
                    recalculateCompletionRate(snapshot);
                    snapshotRepository.update(snapshot);
                });
            }
            case SUBGOAL_STATUS_CHANGED -> {
                snapshotRepository.findByGoalId(goalId).ifPresent(snapshot -> {
                    String subGoalId = event.entityId().toString();
                    snapshot.getSubGoals().stream()
                        .filter(sg -> sg.getSubGoalId().equals(subGoalId))
                        .findFirst()
                        .ifPresent(sg -> {
                            if (!sg.getStatusHistory().isEmpty()) {
                                StatusPeriod last = sg.getStatusHistory().getLast();
                                if (last.getExitedAt() == null) {
                                    last.setExitedAt(event.occurredAt());
                                    last.setDurationMs(event.occurredAt().toEpochMilli() - last.getEnteredAt().toEpochMilli());
                                }
                            }
                            sg.getStatusHistory().add(StatusPeriod.builder()
                                .status(event.newStatus())
                                .enteredAt(event.occurredAt())
                                .build());
                            sg.setCurrentStatus(event.newStatus());
                            if ("COMPLETED".equals(event.newStatus())) {
                                sg.setCompletedAt(event.occurredAt());
                                long totalMs = sg.getStatusHistory().stream()
                                    .filter(p -> p.getDurationMs() != null)
                                    .mapToLong(StatusPeriod::getDurationMs)
                                    .sum();
                                sg.setTotalDurationMs(totalMs);
                            }
                        });
                    recalculateCompletionRate(snapshot);
                    snapshotRepository.update(snapshot);
                });
            }
            case SUBGOAL_UPDATED -> {
                snapshotRepository.findByGoalId(goalId).ifPresent(snapshot -> {
                    String subGoalId = event.entityId().toString();
                    snapshot.getSubGoals().stream()
                        .filter(sg -> sg.getSubGoalId().equals(subGoalId))
                        .findFirst()
                        .ifPresent(sg -> {
                            if (event.title() != null) sg.setTitle(event.title());
                        });
                    snapshotRepository.update(snapshot);
                });
            }
            case SUBGOAL_DELETED -> {
                snapshotRepository.findByGoalId(goalId).ifPresent(snapshot -> {
                    String subGoalId = event.entityId().toString();
                    snapshot.getSubGoals().removeIf(sg -> sg.getSubGoalId().equals(subGoalId));
                    snapshot.setTotalSubGoals(snapshot.getSubGoals().size());
                    recalculateCompletionRate(snapshot);
                    snapshotRepository.update(snapshot);
                });
            }
        }
    }

    private void recalculateCompletionRate(GoalSnapshot snapshot) {
        long completed = snapshot.getSubGoals().stream()
            .filter(sg -> "COMPLETED".equals(sg.getCurrentStatus()))
            .count();
        snapshot.setCompletedSubGoals((int) completed);
        snapshot.setCompletionRate(snapshot.getTotalSubGoals() > 0
            ? (double) completed / snapshot.getTotalSubGoals()
            : 0.0);
    }

    @Override
    public GoalSnapshot getGoalSnapshot(String goalId, UUID userId) {
        return snapshotRepository.findByGoalId(goalId)
            .filter(s -> s.getUserId().equals(userId.toString()))
            .orElseThrow(() -> new ResourceNotFoundException("GoalSnapshot", goalId));
    }

    @Override
    public List<GoalEventDocument> getGoalTimeline(String goalId, UUID userId) {
        return eventRepository.findByGoalIdOrderByOccurredAt(goalId).stream()
            .filter(e -> e.getUserId().equals(userId.toString()))
            .toList();
    }

    @Override
    public List<GoalSnapshot> getUserSummary(UUID userId) {
        return snapshotRepository.findByUserId(userId.toString());
    }
}