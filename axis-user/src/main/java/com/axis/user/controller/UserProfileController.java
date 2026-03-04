package com.axis.user.controller;

import com.axis.user.model.dto.UserProfileRequest;
import com.axis.user.model.dto.UserProfileResponse;
import com.axis.user.service.UserProfileService;
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
@Path("/api/users/me")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RunOnVirtualThread
@Tag(name = "User Profile", description = "Manage the authenticated user's profile")
public class UserProfileController {

    @Inject
    UserProfileService profileService;

    @Operation(summary = "Get current user's profile", description = "Returns the profile, creating a blank one if it does not exist yet")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Profile retrieved"),
        @APIResponse(responseCode = "401", description = "Not authenticated")
    })
    @GET
    public UserProfileResponse get() {
        log.debug("Getting profile for current user");
        return profileService.get();
    }

    @Operation(summary = "Create or update profile", description = "Upserts the current user's profile — null fields are ignored")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Profile saved"),
        @APIResponse(responseCode = "400", description = "Invalid request"),
        @APIResponse(responseCode = "401", description = "Not authenticated")
    })
    @PUT
    public UserProfileResponse upsert(@Valid UserProfileRequest request) {
        log.debug("Upserting profile for current user");
        return profileService.upsert(request);
    }

    @Operation(summary = "Delete profile", description = "Removes the current user's profile data")
    @APIResponses({
        @APIResponse(responseCode = "204", description = "Profile deleted"),
        @APIResponse(responseCode = "401", description = "Not authenticated")
    })
    @DELETE
    public Response delete() {
        log.debug("Deleting profile for current user");
        profileService.delete();
        return Response.noContent().build();
    }
}