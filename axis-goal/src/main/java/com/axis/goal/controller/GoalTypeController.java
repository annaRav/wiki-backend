package com.axis.goal.controller;

import com.axis.goal.model.dto.GoalTypeRequest;
import com.axis.goal.model.dto.GoalTypeResponse;
import com.axis.goal.model.dto.PageResponse;
import com.axis.goal.service.GoalTypeService;
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
@Path("/api/goal-types")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RunOnVirtualThread
@Tag(name = "Goal Types", description = "API for configuring goal levels and custom field schemas")
public class GoalTypeController {

    @Inject
    GoalTypeService goalTypeService;

    @Operation(
            summary = "Create new goal type (level)",
            description = "Adds a new layer configuration with custom fields for the user"
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Goal type successfully created"),
            @APIResponse(responseCode = "400", description = "Invalid input data"),
            @APIResponse(responseCode = "401", description = "User not authorized")
    })
    @POST
    public Response create(@Valid GoalTypeRequest request) {
        log.debug("Creating new goal type: {}", request.title());
        GoalTypeResponse response = goalTypeService.create(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @Operation(
            summary = "Update goal type",
            description = "Updates the title or custom field schema"
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Goal type successfully updated"),
            @APIResponse(responseCode = "404", description = "Goal type not found")
    })
    @PUT
    @Path("/{id}")
    public GoalTypeResponse update(
            @Parameter(description = "Goal type ID") @PathParam("id") UUID id,
            @Valid GoalTypeRequest request) {
        log.debug("Updating goal type: {}", id);
        return goalTypeService.update(id, request);
    }

    @Operation(
            summary = "Get goal type by ID",
            description = "Returns the full goal type schema, including custom field definitions"
    )
    @GET
    @Path("/{id}")
    public GoalTypeResponse findById(
            @Parameter(description = "Goal type ID") @PathParam("id") UUID id) {
        log.debug("Finding goal type: {}", id);
        return goalTypeService.findById(id);
    }

    @Operation(
            summary = "Get all goal types",
            description = "Returns a list of all configured levels for the current user"
    )
    @GET
    public PageResponse<GoalTypeResponse> findAll(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("sortBy") @DefaultValue("levelNumber") String sortBy,
            @QueryParam("sortDirection") @DefaultValue("asc") String sortDirection) {
        log.debug("Finding all goal types for user");
        return goalTypeService.findAll(page, size, sortBy, sortDirection);
    }

    @Operation(
            summary = "Delete goal type",
            description = "Deletes the goal type configuration. Warning: this will delete all related goals!"
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "Goal type successfully deleted"),
            @APIResponse(responseCode = "404", description = "Goal type not found")
    })
    @DELETE
    @Path("/{id}")
    public Response delete(
            @Parameter(description = "Goal type ID") @PathParam("id") UUID id) {
        log.debug("Deleting goal type: {}", id);
        goalTypeService.delete(id);
        return Response.noContent().build();
    }
}
