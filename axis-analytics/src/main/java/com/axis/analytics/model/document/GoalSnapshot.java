package com.axis.analytics.model.document;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@MongoEntity(collection = "goal_snapshots")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalSnapshot {
    public String goalId;
    public String userId;
    public String title;
    public String lifeAspectId;
    public String currentStatus;
    public Instant createdAt;
    public Instant completedAt;
    @Builder.Default
    public List<StatusPeriod> statusHistory = new ArrayList<>();
    @Builder.Default
    public List<SubGoalSummary> subGoals = new ArrayList<>();
    public int totalSubGoals;
    public int completedSubGoals;
    public double completionRate;
}