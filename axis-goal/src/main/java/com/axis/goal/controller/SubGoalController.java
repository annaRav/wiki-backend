package com.axis.goal.controller;

import com.axis.goal.model.dto.PageResponse;
import com.axis.goal.model.dto.SubGoalRequest;
import com.axis.goal.model.dto.SubGoalResponse;
import com.axis.goal.service.SubGoalService;
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
@Path("/api/sub-goals")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RunOnVirtualThread
@Tag(name = "Sub-Goals", description = "Sub-goal management API")
public class SubGoalController {

    @Inject
    SubGoalService subGoalService;

    @POST
    @Operation(summary = "Create a sub-goal", description = "Creates a new sub-goal for the authenticated user")
    @APIResponses({
        @APIResponse(responseCode = "201", description = "Sub-goal created successfully"),
        @APIResponse(responseCode = "400", description = "Invalid request data"),
        @APIResponse(responseCode = "401", description = "User not authenticated"),
        @APIResponse(responseCode = "404", description = "Parent goal not found")
    })
    public Response create(@Valid SubGoalRequest request) {
        log.debug("Creating new sub-goal");
        SubGoalResponse response = subGoalService.create(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PATCH
    @Path("/{id}")
    @Operation(summary = "Partially update a sub-goal", description = "Updates only provided fields")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Sub-goal updated successfully"),
        @APIResponse(responseCode = "404", description = "Sub-goal not found")
    })
    public SubGoalResponse patch(
            @Parameter(description = "Sub-Goal ID") @PathParam("id") UUID id,
            SubGoalRequest request) {
        log.debug("Patching sub-goal: {}", id);
        return subGoalService.patch(id, request);
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get sub-goal by ID")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Sub-goal retrieved successfully"),
        @APIResponse(responseCode = "404", description = "Sub-goal not found")
    })
    public SubGoalResponse findById(
            @Parameter(description = "Sub-Goal ID") @PathParam("id") UUID id) {
        log.debug("Finding sub-goal: {}", id);
        return subGoalService.findById(id);
    }

    @GET
    @Operation(summary = "Get all sub-goals", description = "Retrieves all sub-goals for the authenticated user with pagination")
    @APIResponse(responseCode = "200", description = "Sub-goals retrieved successfully")
    public PageResponse<SubGoalResponse> findAll(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
            @QueryParam("sortDirection") @DefaultValue("desc") String sortDirection) {
        log.debug("Finding all sub-goals");
        return subGoalService.findAll(page, size, sortBy, sortDirection);
    }

    @GET
    @Path("/goal/{goalId}")
    @Operation(summary = "Get sub-goals by goal", description = "Retrieves all sub-goals for a specific goal")
    @APIResponse(responseCode = "200", description = "Sub-goals retrieved successfully")
    public PageResponse<SubGoalResponse> findByGoalId(
            @Parameter(description = "Goal ID") @PathParam("goalId") UUID goalId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
            @QueryParam("sortDirection") @DefaultValue("desc") String sortDirection) {
        log.debug("Finding sub-goals for goal: {}", goalId);
        return subGoalService.findByGoalId(goalId, page, size, sortBy, sortDirection);
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a sub-goal")
    @APIResponses({
        @APIResponse(responseCode = "204", description = "Sub-goal deleted successfully"),
        @APIResponse(responseCode = "404", description = "Sub-goal not found")
    })
    public Response delete(
            @Parameter(description = "Sub-Goal ID") @PathParam("id") UUID id) {
        log.debug("Deleting sub-goal: {}", id);
        subGoalService.delete(id);
        return Response.noContent().build();
    }
}
