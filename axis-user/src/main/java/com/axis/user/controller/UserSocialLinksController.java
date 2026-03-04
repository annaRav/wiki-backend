package com.axis.user.controller;

import com.axis.user.model.dto.UserSocialLinksRequest;
import com.axis.user.model.dto.UserSocialLinksResponse;
import com.axis.user.service.UserSocialLinksService;
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
@Path("/api/users/me/social")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RunOnVirtualThread
@Tag(name = "User Social Links", description = "Manage the authenticated user's integration handles (Telegram, email)")
public class UserSocialLinksController {

    @Inject
    UserSocialLinksService socialLinksService;

    @Operation(summary = "Get current user's integration links")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Links retrieved"),
        @APIResponse(responseCode = "401", description = "Not authenticated")
    })
    @GET
    public UserSocialLinksResponse get() {
        log.debug("Getting social links for current user");
        return socialLinksService.get();
    }

    @Operation(summary = "Create or update integration links", description = "Upserts Telegram and email handles — null fields are ignored")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Links saved"),
        @APIResponse(responseCode = "400", description = "Invalid request"),
        @APIResponse(responseCode = "401", description = "Not authenticated")
    })
    @PUT
    public UserSocialLinksResponse upsert(@Valid UserSocialLinksRequest request) {
        log.debug("Upserting social links for current user");
        return socialLinksService.upsert(request);
    }

    @Operation(summary = "Remove integration links")
    @APIResponses({
        @APIResponse(responseCode = "204", description = "Links removed"),
        @APIResponse(responseCode = "401", description = "Not authenticated")
    })
    @DELETE
    public Response delete() {
        log.debug("Deleting social links for current user");
        socialLinksService.delete();
        return Response.noContent().build();
    }
}