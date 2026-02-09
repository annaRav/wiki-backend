package com.axis.goal.controller;

import com.axis.goal.model.dto.CustomFieldDefinitionRequest;
import com.axis.goal.model.dto.CustomFieldDefinitionResponse;
import com.axis.goal.service.CustomFieldDefinitionService;
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

import java.util.List;
import java.util.UUID;

@Slf4j
@Path("/api/goal-types/{goalTypeId}/custom-fields")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RunOnVirtualThread
@Tag(name = "Custom Field Definitions", description = "API for managing custom field definitions for goal types")
public class CustomFieldDefinitionController {

    @Inject
    CustomFieldDefinitionService definitionService;

    @Operation(
            summary = "Create a custom field definition",
            description = "Creates a new custom field definition for a specific goal type"
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Custom field definition created successfully"),
            @APIResponse(responseCode = "400", description = "Invalid request data"),
            @APIResponse(responseCode = "401", description = "User not authenticated"),
            @APIResponse(responseCode = "404", description = "Goal type not found"),
            @APIResponse(responseCode = "409", description = "Custom field key already exists")
    })
    @POST
    public Response create(
            @Parameter(description = "Goal Type ID") @PathParam("goalTypeId") UUID goalTypeId,
            @Valid CustomFieldDefinitionRequest request) {
        log.debug("Creating custom field definition for goal type: {}", goalTypeId);
        CustomFieldDefinitionResponse response = definitionService.create(goalTypeId, request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @Operation(
            summary = "Update a custom field definition",
            description = "Updates an existing custom field definition. Only the owner can update."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Custom field definition updated successfully"),
            @APIResponse(responseCode = "400", description = "Invalid request data"),
            @APIResponse(responseCode = "401", description = "User not authenticated"),
            @APIResponse(responseCode = "403", description = "User doesn't have permission"),
            @APIResponse(responseCode = "404", description = "Custom field definition not found"),
            @APIResponse(responseCode = "409", description = "Custom field key already exists")
    })
    @PUT
    @Path("/{id}")
    public CustomFieldDefinitionResponse update(
            @Parameter(description = "Goal Type ID") @PathParam("goalTypeId") UUID goalTypeId,
            @Parameter(description = "Custom Field Definition ID") @PathParam("id") UUID id,
            @Valid CustomFieldDefinitionRequest request) {
        log.debug("Updating custom field definition: {}", id);
        return definitionService.update(id, request);
    }

    @Operation(
            summary = "Get custom field definition by ID",
            description = "Retrieves a specific custom field definition. Only the owner can view."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Custom field definition retrieved successfully"),
            @APIResponse(responseCode = "401", description = "User not authenticated"),
            @APIResponse(responseCode = "403", description = "User doesn't have permission"),
            @APIResponse(responseCode = "404", description = "Custom field definition not found")
    })
    @GET
    @Path("/{id}")
    public CustomFieldDefinitionResponse findById(
            @Parameter(description = "Goal Type ID") @PathParam("goalTypeId") UUID goalTypeId,
            @Parameter(description = "Custom Field Definition ID") @PathParam("id") UUID id) {
        log.debug("Finding custom field definition: {}", id);
        return definitionService.findById(id);
    }

    @Operation(
            summary = "Get all custom field definitions for a goal type",
            description = "Retrieves all custom field definitions for a specific goal type"
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Custom field definitions retrieved successfully"),
            @APIResponse(responseCode = "401", description = "User not authenticated"),
            @APIResponse(responseCode = "404", description = "Goal type not found")
    })
    @GET
    public List<CustomFieldDefinitionResponse> findByGoalTypeId(
            @Parameter(description = "Goal Type ID") @PathParam("goalTypeId") UUID goalTypeId) {
        log.debug("Finding custom field definitions for goal type: {}", goalTypeId);
        return definitionService.findByGoalTypeId(goalTypeId);
    }

    @Operation(
            summary = "Delete a custom field definition",
            description = "Deletes a custom field definition. Only the owner can delete."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "Custom field definition deleted successfully"),
            @APIResponse(responseCode = "401", description = "User not authenticated"),
            @APIResponse(responseCode = "403", description = "User doesn't have permission"),
            @APIResponse(responseCode = "404", description = "Custom field definition not found")
    })
    @DELETE
    @Path("/{id}")
    public Response delete(
            @Parameter(description = "Goal Type ID") @PathParam("goalTypeId") UUID goalTypeId,
            @Parameter(description = "Custom Field Definition ID") @PathParam("id") UUID id) {
        log.debug("Deleting custom field definition: {}", id);
        definitionService.delete(id);
        return Response.noContent().build();
    }
}
