package com.axis.user.controller;

import com.axis.user.model.dto.UserStatsResponse;
import com.axis.user.service.UserStatsService;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Slf4j
@Path("/api/users/me/stats")
@Produces(MediaType.APPLICATION_JSON)
@RunOnVirtualThread
@Tag(name = "User Stats", description = "Read-only goal statistics for the authenticated user")
public class UserStatsController {

    @Inject
    UserStatsService statsService;

    @Operation(summary = "Get current user's goal statistics")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Stats retrieved"),
        @APIResponse(responseCode = "401", description = "Not authenticated")
    })
    @GET
    public UserStatsResponse get() {
        log.debug("Getting stats for current user");
        return statsService.get();
    }
}