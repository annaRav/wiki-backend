package com.axis.goal.controller;

import com.axis.goal.model.dto.GoalRequest;
import com.axis.goal.model.dto.GoalResponse;
import com.axis.goal.model.dto.PageResponse;
import com.axis.goal.model.enums.ProgressStatus;
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
@Tag(name = "Goals", description = "Goal management API")
public class GoalController {

    @Inject
    GoalService goalService;

    @POST
    @Operation(summary = "Create a new goal")
    @APIResponses({
        @APIResponse(responseCode = "201", description = "Goal created successfully"),
        @APIResponse(responseCode = "400", description = "Invalid request data"),
        @APIResponse(responseCode = "404", description = "Life aspect not found")
    })
    public Response create(@Valid GoalRequest request) {
        log.debug("Creating new goal");
        return Response.status(Response.Status.CREATED).entity(goalService.create(request)).build();
    }

    @PATCH
    @Path("/{id}")
    @Operation(summary = "Partially update a goal")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Goal updated successfully"),
        @APIResponse(responseCode = "404", description = "Goal not found")
    })
    public GoalResponse patch(
            @Parameter(description = "Goal ID") @PathParam("id") UUID id,
            GoalRequest request) {
        log.debug("Patching goal: {}", id);
        return goalService.patch(id, request);
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get goal by ID")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Goal retrieved successfully"),
        @APIResponse(responseCode = "404", description = "Goal not found")
    })
    public GoalResponse findById(
            @Parameter(description = "Goal ID") @PathParam("id") UUID id) {
        return goalService.findById(id);
    }

    @GET
    @Operation(summary = "Get all goals with pagination")
    @APIResponse(responseCode = "200", description = "Goals retrieved successfully")
    public PageResponse<GoalResponse> findAll(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
            @QueryParam("sortDirection") @DefaultValue("desc") String sortDirection) {
        return goalService.findAll(page, size, sortBy, sortDirection);
    }

    @GET
    @Path("/status/{status}")
    @Operation(summary = "Get goals by progress status")
    @APIResponse(responseCode = "200", description = "Goals retrieved successfully")
    public PageResponse<GoalResponse> findByStatus(
            @Parameter(description = "Progress status") @PathParam("status") ProgressStatus status,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
            @QueryParam("sortDirection") @DefaultValue("desc") String sortDirection) {
        log.debug("Finding goals with status: {}", status);
        return goalService.findByStatus(status, page, size, sortBy, sortDirection);
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a goal")
    @APIResponses({
        @APIResponse(responseCode = "204", description = "Goal deleted successfully"),
        @APIResponse(responseCode = "404", description = "Goal not found")
    })
    public Response delete(
            @Parameter(description = "Goal ID") @PathParam("id") UUID id) {
        goalService.delete(id);
        return Response.noContent().build();
    }
}
