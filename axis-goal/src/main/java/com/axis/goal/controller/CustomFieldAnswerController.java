package com.axis.goal.controller;

import com.axis.goal.model.dto.CustomFieldAnswerRequest;
import com.axis.goal.model.dto.CustomFieldAnswerResponse;
import com.axis.goal.service.CustomFieldAnswerService;
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
@Path("/api/custom-field-answers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RunOnVirtualThread
@Tag(name = "Custom Field Answers", description = "API for managing custom field answers")
public class CustomFieldAnswerController {

    @Inject
    CustomFieldAnswerService answerService;

    @POST
    @Operation(summary = "Create a custom field answer")
    @APIResponses({
        @APIResponse(responseCode = "201", description = "Answer created successfully"),
        @APIResponse(responseCode = "400", description = "Invalid request data"),
        @APIResponse(responseCode = "404", description = "Field definition not found"),
        @APIResponse(responseCode = "409", description = "Answer already exists for this field")
    })
    public Response create(@Valid CustomFieldAnswerRequest request) {
        log.debug("Creating custom field answer for owner: {}", request.ownerId());
        CustomFieldAnswerResponse response = answerService.create(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update a custom field answer (full update)")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Answer updated successfully"),
        @APIResponse(responseCode = "403", description = "Access denied"),
        @APIResponse(responseCode = "404", description = "Answer not found")
    })
    public CustomFieldAnswerResponse update(
            @Parameter(description = "Answer ID") @PathParam("id") UUID id,
            @Valid CustomFieldAnswerRequest request) {
        log.debug("Updating custom field answer: {}", id);
        return answerService.update(id, request);
    }

    @PATCH
    @Path("/{id}")
    @Operation(summary = "Partially update a custom field answer")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Answer updated successfully"),
        @APIResponse(responseCode = "403", description = "Access denied"),
        @APIResponse(responseCode = "404", description = "Answer not found")
    })
    public CustomFieldAnswerResponse patch(
            @Parameter(description = "Answer ID") @PathParam("id") UUID id,
            CustomFieldAnswerRequest request) {
        log.debug("Patching custom field answer: {}", id);
        return answerService.patch(id, request);
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get custom field answer by ID")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Answer retrieved successfully"),
        @APIResponse(responseCode = "403", description = "Access denied"),
        @APIResponse(responseCode = "404", description = "Answer not found")
    })
    public CustomFieldAnswerResponse findById(
            @Parameter(description = "Answer ID") @PathParam("id") UUID id) {
        log.debug("Finding custom field answer: {}", id);
        return answerService.findById(id);
    }

    @GET
    @Operation(summary = "Get all custom field answers for an owner")
    @APIResponse(responseCode = "200", description = "Answers retrieved successfully")
    public List<CustomFieldAnswerResponse> findByOwnerId(
            @Parameter(description = "Owner ID") @QueryParam("ownerId") UUID ownerId) {
        log.debug("Finding custom field answers for owner: {}", ownerId);
        return answerService.findByOwnerId(ownerId);
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a custom field answer")
    @APIResponses({
        @APIResponse(responseCode = "204", description = "Answer deleted successfully"),
        @APIResponse(responseCode = "400", description = "Cannot delete required field answer"),
        @APIResponse(responseCode = "403", description = "Access denied"),
        @APIResponse(responseCode = "404", description = "Answer not found")
    })
    public Response delete(
            @Parameter(description = "Answer ID") @PathParam("id") UUID id) {
        log.debug("Deleting custom field answer: {}", id);
        answerService.delete(id);
        return Response.noContent().build();
    }
}
