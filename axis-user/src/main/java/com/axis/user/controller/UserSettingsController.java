package com.axis.user.controller;

import com.axis.user.model.dto.UserSettingsRequest;
import com.axis.user.model.dto.UserSettingsResponse;
import com.axis.user.service.UserSettingsService;
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
@Path("/api/users/me/settings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RunOnVirtualThread
@Tag(name = "User Settings", description = "Manage the authenticated user's app preferences")
public class UserSettingsController {

    @Inject
    UserSettingsService settingsService;

    @Operation(summary = "Get current user's settings", description = "Returns settings with defaults if none have been saved yet")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Settings retrieved"),
        @APIResponse(responseCode = "401", description = "Not authenticated")
    })
    @GET
    public UserSettingsResponse get() {
        log.debug("Getting settings for current user");
        return settingsService.get();
    }

    @Operation(summary = "Create or update settings", description = "Upserts the current user's settings — null fields are ignored")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Settings saved"),
        @APIResponse(responseCode = "400", description = "Invalid request"),
        @APIResponse(responseCode = "401", description = "Not authenticated")
    })
    @PUT
    public UserSettingsResponse upsert(@Valid UserSettingsRequest request) {
        log.debug("Upserting settings for current user");
        return settingsService.upsert(request);
    }

    @Operation(summary = "Reset settings to defaults", description = "Deletes saved settings so defaults are returned on next GET")
    @APIResponses({
        @APIResponse(responseCode = "204", description = "Settings reset"),
        @APIResponse(responseCode = "401", description = "Not authenticated")
    })
    @DELETE
    public Response delete() {
        log.debug("Deleting settings for current user");
        settingsService.delete();
        return Response.noContent().build();
    }
}