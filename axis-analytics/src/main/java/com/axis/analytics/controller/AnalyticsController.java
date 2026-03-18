package com.axis.analytics.controller;

import com.axis.analytics.model.document.GoalEventDocument;
import com.axis.analytics.model.document.GoalSnapshot;
import com.axis.analytics.service.AnalyticsService;
import com.axis.common.security.SecurityUtils;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@Path("/api/analytics")
@Authenticated
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Analytics", description = "Goal analytics and progress tracking")
public class AnalyticsController {

    @Inject
    AnalyticsService analyticsService;

    @Inject
    SecurityUtils securityUtils;

    @GET
    @Path("/goals/{goalId}/timeline")
    @Operation(summary = "Get full event timeline for a goal")
    public List<GoalEventDocument> getTimeline(@PathParam("goalId") String goalId) {
        UUID userId = securityUtils.getCurrentUserIdAsUUID().orElseThrow();
        return analyticsService.getGoalTimeline(goalId, userId);
    }

    @GET
    @Path("/goals/{goalId}/progress")
    @Operation(summary = "Get goal progress snapshot with time in each status")
    public GoalSnapshot getProgress(@PathParam("goalId") String goalId) {
        UUID userId = securityUtils.getCurrentUserIdAsUUID().orElseThrow();
        return analyticsService.getGoalSnapshot(goalId, userId);
    }

    @GET
    @Path("/goals/summary")
    @Operation(summary = "Get summary for all user goals")
    public List<GoalSnapshot> getSummary() {
        UUID userId = securityUtils.getCurrentUserIdAsUUID().orElseThrow();
        return analyticsService.getUserSummary(userId);
    }
}