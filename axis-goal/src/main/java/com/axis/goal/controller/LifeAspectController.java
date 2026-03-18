package com.axis.goal.controller;

import com.axis.goal.model.dto.GoalResponse;
import com.axis.goal.model.dto.LifeAspectRequest;
import com.axis.goal.model.dto.LifeAspectResponse;
import com.axis.goal.model.dto.PageResponse;
import com.axis.goal.service.GoalService;
import com.axis.goal.service.LifeAspectService;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@Slf4j
@Path("/api/life-aspects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RunOnVirtualThread
@Tag(name = "Life Aspects", description = "Life aspect management API — top-level goal categories")
public class LifeAspectController {

    @Inject
    LifeAspectService lifeAspectService;

    @Inject
    GoalService goalService;

    @POST
    @Operation(summary = "Create a life aspect", description = "Creates a new life aspect for the authenticated user")
    @APIResponses({
        @APIResponse(responseCode = "201", description = "Life aspect created successfully"),
        @APIResponse(responseCode = "400", description = "Invalid request data"),
        @APIResponse(responseCode = "401", description = "User not authenticated")
    })
    public Response create(@Valid LifeAspectRequest request) {
        log.debug("Creating new life aspect");
        LifeAspectResponse response = lifeAspectService.create(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PATCH
    @Path("/{id}")
    @Operation(summary = "Partially update a life aspect", description = "Updates only provided fields")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Life aspect updated successfully"),
        @APIResponse(responseCode = "404", description = "Life aspect not found")
    })
    public LifeAspectResponse patch(
            @Parameter(description = "Life Aspect ID") @PathParam("id") UUID id,
            LifeAspectRequest request) {
        log.debug("Patching life aspect: {}", id);
        return lifeAspectService.patch(id, request);
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get life aspect by ID")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Life aspect retrieved successfully"),
        @APIResponse(responseCode = "404", description = "Life aspect not found")
    })
    public LifeAspectResponse findById(
            @Parameter(description = "Life Aspect ID") @PathParam("id") UUID id) {
        log.debug("Finding life aspect: {}", id);
        return lifeAspectService.findById(id);
    }

    @GET
    @Operation(summary = "Get all life aspects", description = "Retrieves all life aspects for the authenticated user with pagination")
    @APIResponse(responseCode = "200", description = "Life aspects retrieved successfully")
    public PageResponse<LifeAspectResponse> findAll(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
            @QueryParam("sortDirection") @DefaultValue("desc") String sortDirection) {
        log.debug("Finding all life aspects");
        return lifeAspectService.findAll(page, size, sortBy, sortDirection);
    }

    @GET
    @Path("/{id}/goals")
    @Operation(summary = "Get goals for a life aspect", description = "Retrieves all goals belonging to this life aspect")
    @APIResponse(responseCode = "200", description = "Goals retrieved successfully")
    public PageResponse<GoalResponse> findGoals(
            @Parameter(description = "Life Aspect ID") @PathParam("id") UUID id,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
            @QueryParam("sortDirection") @DefaultValue("desc") String sortDirection) {
        log.debug("Finding goals for life aspect: {}", id);
        return goalService.findByLifeAspectId(id, page, size, sortBy, sortDirection);
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a life aspect", description = "Deletes a life aspect and all its goals")
    @APIResponses({
        @APIResponse(responseCode = "204", description = "Life aspect deleted successfully"),
        @APIResponse(responseCode = "404", description = "Life aspect not found")
    })
    public Response delete(
            @Parameter(description = "Life Aspect ID") @PathParam("id") UUID id) {
        log.debug("Deleting life aspect: {}", id);
        lifeAspectService.delete(id);
        return Response.noContent().build();
    }
}
