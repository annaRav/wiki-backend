package com.axis.goal.controller;

import com.axis.goal.model.dto.CustomFieldDefinitionRequest;
import com.axis.goal.model.dto.CustomFieldDefinitionResponse;
import com.axis.goal.model.enums.OwnerType;
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
@Path("/api/custom-field-definitions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RunOnVirtualThread
@Tag(name = "Custom Field Definitions", description = "API for managing custom field definitions per entity type")
public class CustomFieldDefinitionController {

    @Inject
    CustomFieldDefinitionService definitionService;

    @POST
    @Operation(summary = "Create a custom field definition",
               description = "Creates a new custom field definition for the specified entity type (LIFE_ASPECT, GOAL, SUB_GOAL)")
    @APIResponses({
        @APIResponse(responseCode = "201", description = "Custom field definition created successfully"),
        @APIResponse(responseCode = "400", description = "Invalid request data"),
        @APIResponse(responseCode = "401", description = "User not authenticated")
    })
    public Response create(@Valid CustomFieldDefinitionRequest request) {
        log.debug("Creating custom field definition for owner type: {}", request.ownerType());
        CustomFieldDefinitionResponse response = definitionService.create(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update a custom field definition (full update)")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Custom field definition updated successfully"),
        @APIResponse(responseCode = "403", description = "Access denied"),
        @APIResponse(responseCode = "404", description = "Custom field definition not found")
    })
    public CustomFieldDefinitionResponse update(
            @Parameter(description = "Definition ID") @PathParam("id") UUID id,
            @Valid CustomFieldDefinitionRequest request) {
        log.debug("Updating custom field definition: {}", id);
        return definitionService.update(id, request);
    }

    @PATCH
    @Path("/{id}")
    @Operation(summary = "Partially update a custom field definition")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Custom field definition updated successfully"),
        @APIResponse(responseCode = "403", description = "Access denied"),
        @APIResponse(responseCode = "404", description = "Custom field definition not found")
    })
    public CustomFieldDefinitionResponse patch(
            @Parameter(description = "Definition ID") @PathParam("id") UUID id,
            CustomFieldDefinitionRequest request) {
        log.debug("Patching custom field definition: {}", id);
        return definitionService.patch(id, request);
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get custom field definition by ID")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Custom field definition retrieved successfully"),
        @APIResponse(responseCode = "403", description = "Access denied"),
        @APIResponse(responseCode = "404", description = "Custom field definition not found")
    })
    public CustomFieldDefinitionResponse findById(
            @Parameter(description = "Definition ID") @PathParam("id") UUID id) {
        log.debug("Finding custom field definition: {}", id);
        return definitionService.findById(id);
    }

    @GET
    @Operation(summary = "Get all custom field definitions by entity type",
               description = "Retrieves all custom field definitions for a specific entity type (LIFE_ASPECT, GOAL, or SUB_GOAL)")
    @APIResponse(responseCode = "200", description = "Custom field definitions retrieved successfully")
    public List<CustomFieldDefinitionResponse> findByOwnerType(
            @Parameter(description = "Owner type: LIFE_ASPECT, GOAL, or SUB_GOAL")
            @QueryParam("ownerType") OwnerType ownerType) {
        log.debug("Finding custom field definitions for owner type: {}", ownerType);
        return definitionService.findByOwnerType(ownerType);
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a custom field definition")
    @APIResponses({
        @APIResponse(responseCode = "204", description = "Custom field definition deleted successfully"),
        @APIResponse(responseCode = "403", description = "Access denied"),
        @APIResponse(responseCode = "404", description = "Custom field definition not found")
    })
    public Response delete(
            @Parameter(description = "Definition ID") @PathParam("id") UUID id) {
        log.debug("Deleting custom field definition: {}", id);
        definitionService.delete(id);
        return Response.noContent().build();
    }
}
