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
@Path("/api/goals/{goalId}/custom-field-answers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RunOnVirtualThread
@Tag(name = "Custom Field Answers", description = "API for managing custom field answers for goals")
public class CustomFieldAnswerController {

    @Inject
    CustomFieldAnswerService answerService;

    @Operation(
            summary = "Create a custom field answer",
            description = "Creates a new custom field answer for a specific goal"
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Custom field answer created successfully"),
            @APIResponse(responseCode = "400", description = "Invalid request data or field doesn't belong to goal's type"),
            @APIResponse(responseCode = "401", description = "User not authenticated"),
            @APIResponse(responseCode = "404", description = "Goal or field definition not found"),
            @APIResponse(responseCode = "409", description = "Answer already exists for this field")
    })
    @POST
    public Response create(
            @Parameter(description = "Goal ID") @PathParam("goalId") UUID goalId,
            @Valid CustomFieldAnswerRequest request) {
        log.debug("Creating custom field answer for goal: {}", goalId);
        CustomFieldAnswerResponse response = answerService.create(goalId, request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @Operation(
            summary = "Update a custom field answer",
            description = "Updates an existing custom field answer. Only the owner can update."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Custom field answer updated successfully"),
            @APIResponse(responseCode = "400", description = "Invalid request data"),
            @APIResponse(responseCode = "401", description = "User not authenticated"),
            @APIResponse(responseCode = "403", description = "User doesn't have permission"),
            @APIResponse(responseCode = "404", description = "Custom field answer not found")
    })
    @PUT
    @Path("/{id}")
    public CustomFieldAnswerResponse update(
            @Parameter(description = "Goal ID") @PathParam("goalId") UUID goalId,
            @Parameter(description = "Custom Field Answer ID") @PathParam("id") UUID id,
            @Valid CustomFieldAnswerRequest request) {
        log.debug("Updating custom field answer: {}", id);
        return answerService.update(id, request);
    }

    @Operation(
            summary = "Get custom field answer by ID",
            description = "Retrieves a specific custom field answer. Only the owner can view."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Custom field answer retrieved successfully"),
            @APIResponse(responseCode = "401", description = "User not authenticated"),
            @APIResponse(responseCode = "403", description = "User doesn't have permission"),
            @APIResponse(responseCode = "404", description = "Custom field answer not found")
    })
    @GET
    @Path("/{id}")
    public CustomFieldAnswerResponse findById(
            @Parameter(description = "Goal ID") @PathParam("goalId") UUID goalId,
            @Parameter(description = "Custom Field Answer ID") @PathParam("id") UUID id) {
        log.debug("Finding custom field answer: {}", id);
        return answerService.findById(id);
    }

    @Operation(
            summary = "Get all custom field answers for a goal",
            description = "Retrieves all custom field answers for a specific goal"
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Custom field answers retrieved successfully"),
            @APIResponse(responseCode = "401", description = "User not authenticated"),
            @APIResponse(responseCode = "404", description = "Goal not found")
    })
    @GET
    public List<CustomFieldAnswerResponse> findByGoalId(
            @Parameter(description = "Goal ID") @PathParam("goalId") UUID goalId) {
        log.debug("Finding custom field answers for goal: {}", goalId);
        return answerService.findByGoalId(goalId);
    }

    @Operation(
            summary = "Delete a custom field answer",
            description = "Deletes a custom field answer. Only the owner can delete. Cannot delete required fields."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "Custom field answer deleted successfully"),
            @APIResponse(responseCode = "400", description = "Cannot delete required field answer"),
            @APIResponse(responseCode = "401", description = "User not authenticated"),
            @APIResponse(responseCode = "403", description = "User doesn't have permission"),
            @APIResponse(responseCode = "404", description = "Custom field answer not found")
    })
    @DELETE
    @Path("/{id}")
    public Response delete(
            @Parameter(description = "Goal ID") @PathParam("goalId") UUID goalId,
            @Parameter(description = "Custom Field Answer ID") @PathParam("id") UUID id) {
        log.debug("Deleting custom field answer: {}", id);
        answerService.delete(id);
        return Response.noContent().build();
    }
}
