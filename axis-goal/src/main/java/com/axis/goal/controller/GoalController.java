package com.axis.goal.controller;

import com.axis.goal.model.dto.GoalRequest;
import com.axis.goal.model.dto.GoalResponse;
import com.axis.goal.model.dto.PageResponse;
import com.axis.goal.model.entity.Goal.GoalStatus;
import com.axis.goal.service.GoalService;
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
@Path("/api/goals")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RunOnVirtualThread
@Tag(name = "Goals", description = "Goal management API for creating, tracking, and organizing life goals")
public class GoalController {

    @Inject
    GoalService goalService;

    @Operation(
        summary = "Create a new goal",
        description = "Creates a new goal for the authenticated user"
    )
    @APIResponses(value = {
        @APIResponse(responseCode = "201", description = "Goal created successfully"),
        @APIResponse(responseCode = "400", description = "Invalid request data"),
        @APIResponse(responseCode = "401", description = "User not authenticated")
    })
    @POST
    public Response create(@Valid GoalRequest request) {
        log.debug("Creating new goal");
        GoalResponse response = goalService.create(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @Operation(
        summary = "Update an existing goal",
        description = "Updates an existing goal. Only the owner can update their goals."
    )
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Goal updated successfully"),
        @APIResponse(responseCode = "400", description = "Invalid request data"),
        @APIResponse(responseCode = "401", description = "User not authenticated"),
        @APIResponse(responseCode = "404", description = "Goal not found")
    })
    @PUT
    @Path("/{id}")
    public GoalResponse update(
            @Parameter(description = "Goal ID") @PathParam("id") UUID id,
            @Valid GoalRequest request) {
        log.debug("Updating goal: {}", id);
        return goalService.update(id, request);
    }

    @Operation(
        summary = "Get goal by ID",
        description = "Retrieves a specific goal by its ID. Only the owner can view their goals."
    )
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Goal retrieved successfully"),
        @APIResponse(responseCode = "401", description = "User not authenticated"),
        @APIResponse(responseCode = "404", description = "Goal not found")
    })
    @GET
    @Path("/{id}")
    public GoalResponse findById(
            @Parameter(description = "Goal ID") @PathParam("id") UUID id) {
        log.debug("Finding goal: {}", id);
        return goalService.findById(id);
    }

    @Operation(
        summary = "Get all goals",
        description = "Retrieves all goals for the authenticated user with pagination"
    )
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Goals retrieved successfully"),
        @APIResponse(responseCode = "401", description = "User not authenticated")
    })
    @GET
    public PageResponse<GoalResponse> findAll(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
            @QueryParam("sortDirection") @DefaultValue("desc") String sortDirection) {
        log.debug("Finding all goals");
        return goalService.findAll(page, size, sortBy, sortDirection);
    }

    @Operation(
        summary = "Get goals by status",
        description = "Retrieves goals filtered by status for the authenticated user"
    )
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Goals retrieved successfully"),
        @APIResponse(responseCode = "401", description = "User not authenticated")
    })
    @GET
    @Path("/status/{status}")
    public PageResponse<GoalResponse> findByStatus(
            @Parameter(description = "Goal status") @PathParam("status") GoalStatus status,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
            @QueryParam("sortDirection") @DefaultValue("desc") String sortDirection) {
        log.debug("Finding goals with status: {}", status);
        return goalService.findByStatus(status, page, size, sortBy, sortDirection);
    }

    @Operation(
        summary = "Get goals by type ID",
        description = "Retrieves goals filtered by goal type ID for the authenticated user"
    )
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Goals retrieved successfully"),
        @APIResponse(responseCode = "401", description = "User not authenticated")
    })
    @GET
    @Path("/type/{typeId}")
    public PageResponse<GoalResponse> findByTypeId(
            @Parameter(description = "Goal type ID") @PathParam("typeId") UUID typeId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
            @QueryParam("sortDirection") @DefaultValue("desc") String sortDirection) {
        log.debug("Finding goals with type ID: {}", typeId);
        return goalService.findByTypeId(typeId, page, size, sortBy, sortDirection);
    }

    @Operation(
        summary = "Delete a goal",
        description = "Deletes a goal. Only the owner can delete their goals."
    )
    @APIResponses(value = {
        @APIResponse(responseCode = "204", description = "Goal deleted successfully"),
        @APIResponse(responseCode = "401", description = "User not authenticated"),
        @APIResponse(responseCode = "404", description = "Goal not found")
    })
    @DELETE
    @Path("/{id}")
    public Response delete(
            @Parameter(description = "Goal ID") @PathParam("id") UUID id) {
        log.debug("Deleting goal: {}", id);
        goalService.delete(id);
        return Response.noContent().build();
    }
}
