package com.axis.analytics.service;

import com.axis.analytics.model.document.GoalEventDocument;
import com.axis.analytics.model.document.GoalSnapshot;
import com.axis.common.event.GoalDomainEvent;
import java.util.List;
import java.util.UUID;

public interface AnalyticsService {
    void processEvent(GoalDomainEvent event);
    GoalSnapshot getGoalSnapshot(String goalId, UUID userId);
    List<GoalEventDocument> getGoalTimeline(String goalId, UUID userId);
    List<GoalSnapshot> getUserSummary(UUID userId);
}