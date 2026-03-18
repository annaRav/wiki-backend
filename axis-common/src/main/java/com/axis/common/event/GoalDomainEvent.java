package com.axis.common.event;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record GoalDomainEvent(
    UUID eventId,
    GoalEventType eventType,
    String entityType,
    UUID entityId,
    UUID goalId,
    UUID userId,
    String lifeAspectId,
    String previousStatus,
    String newStatus,
    String title,
    String description,
    Map<String, Object> changes,
    Instant occurredAt
) {}