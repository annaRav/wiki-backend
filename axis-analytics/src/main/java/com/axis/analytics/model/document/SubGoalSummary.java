package com.axis.analytics.model.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubGoalSummary {
    private String subGoalId;
    private String title;
    private String currentStatus;
    private Instant createdAt;
    private Instant completedAt;
    private Long totalDurationMs;
    @Builder.Default
    private List<StatusPeriod> statusHistory = new ArrayList<>();
}