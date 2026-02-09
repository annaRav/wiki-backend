package com.axis.notification.controller;

import com.axis.notification.model.dto.NotificationSettingsRequest;
import com.axis.notification.model.dto.NotificationSettingsResponse;
import com.axis.notification.service.NotificationSettingsService;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Slf4j
@Path("/api/notifications/settings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RunOnVirtualThread
@Tag(name = "Notification Settings", description = "Endpoints for managing user notification preferences")
public class NotificationSettingsController {

    @Inject
    NotificationSettingsService service;

    @PUT
    @Operation(summary = "Create or update notification settings",
            description = "Creates or updates notification preferences for the current user")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Settings saved successfully"),
            @APIResponse(responseCode = "400", description = "Invalid request data"),
            @APIResponse(responseCode = "401", description = "User not authenticated")
    })
    public NotificationSettingsResponse createOrUpdate(@Valid NotificationSettingsRequest request) {
        log.info("Received request to create or update notification settings");
        return service.createOrUpdate(request);
    }

    @GET
    @Operation(summary = "Get notification settings",
            description = "Retrieves notification preferences for the current user (returns defaults if none exist)")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Settings retrieved successfully"),
            @APIResponse(responseCode = "401", description = "User not authenticated")
    })
    public NotificationSettingsResponse getOrCreateForCurrentUser() {
        log.info("Received request to find notification settings for current user");
        return service.getOrCreateForCurrentUser();
    }

    @DELETE
    @Operation(summary = "Delete notification settings",
            description = "Deletes notification preferences for the current user (will revert to defaults)")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Settings deleted successfully"),
            @APIResponse(responseCode = "401", description = "User not authenticated")
    })
    public Response deleteForCurrentUser() {
        log.info("Received request to delete notification settings for current user");
        service.deleteForCurrentUser();
        return Response.noContent().build();
    }
}
