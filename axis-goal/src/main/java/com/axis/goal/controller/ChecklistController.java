package com.axis.goal.controller;

import com.axis.goal.model.dto.*;
import com.axis.goal.service.ChecklistService;
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
@Path("/api/checklists")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RunOnVirtualThread
@Tag(name = "Checklists", description = "Checklist management for goals and sub-goals")
public class ChecklistController {

    @Inject
    ChecklistService checklistService;

    @POST
    @Operation(summary = "Create a checklist")
    @APIResponses({
        @APIResponse(responseCode = "201", description = "Checklist created successfully"),
        @APIResponse(responseCode = "400", description = "Invalid request data"),
        @APIResponse(responseCode = "404", description = "Owner not found")
    })
    public Response createChecklist(@Valid ChecklistRequest request) {
        log.debug("Creating checklist for owner: {} ({})", request.ownerId(), request.ownerType());
        ChecklistResponse response = checklistService.createChecklist(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @Operation(summary = "Get all checklists for an owner")
    @APIResponse(responseCode = "200", description = "Checklists retrieved successfully")
    public List<ChecklistResponse> findAllChecklists(
            @Parameter(description = "Owner ID (goal or sub-goal)") @QueryParam("ownerId") UUID ownerId) {
        log.debug("Finding checklists for owner: {}", ownerId);
        return checklistService.findAllChecklists(ownerId);
    }

    @PATCH
    @Path("/{checklistId}")
    @Operation(summary = "Update a checklist")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Checklist updated successfully"),
        @APIResponse(responseCode = "404", description = "Checklist not found")
    })
    public ChecklistResponse patchChecklist(
            @Parameter(description = "Checklist ID") @PathParam("checklistId") UUID checklistId,
            ChecklistRequest request) {
        log.debug("Patching checklist: {}", checklistId);
        return checklistService.patchChecklist(checklistId, request);
    }

    @DELETE
    @Path("/{checklistId}")
    @Operation(summary = "Delete a checklist")
    @APIResponse(responseCode = "204", description = "Checklist deleted successfully")
    public Response deleteChecklist(
            @Parameter(description = "Checklist ID") @PathParam("checklistId") UUID checklistId) {
        log.debug("Deleting checklist: {}", checklistId);
        checklistService.deleteChecklist(checklistId);
        return Response.noContent().build();
    }

    @POST
    @Path("/{checklistId}/items")
    @Operation(summary = "Add an item to a checklist")
    @APIResponse(responseCode = "201", description = "Item created successfully")
    public Response createItem(
            @Parameter(description = "Checklist ID") @PathParam("checklistId") UUID checklistId,
            @Valid ChecklistItemRequest request) {
        log.debug("Creating item in checklist: {}", checklistId);
        ChecklistItemResponse response = checklistService.createItem(checklistId, request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PATCH
    @Path("/{checklistId}/items/{itemId}")
    @Operation(summary = "Update a checklist item")
    @APIResponse(responseCode = "200", description = "Item updated successfully")
    public ChecklistItemResponse patchItem(
            @Parameter(description = "Checklist ID") @PathParam("checklistId") UUID checklistId,
            @Parameter(description = "Item ID") @PathParam("itemId") UUID itemId,
            ChecklistItemRequest request) {
        log.debug("Patching item: {} in checklist: {}", itemId, checklistId);
        return checklistService.patchItem(checklistId, itemId, request);
    }

    @PATCH
    @Path("/{checklistId}/items/{itemId}/position")
    @Operation(summary = "Reorder a checklist item")
    @APIResponse(responseCode = "200", description = "Item reordered successfully")
    public ChecklistItemResponse reorderItem(
            @Parameter(description = "Checklist ID") @PathParam("checklistId") UUID checklistId,
            @Parameter(description = "Item ID") @PathParam("itemId") UUID itemId,
            @Valid ReorderItemRequest request) {
        log.debug("Reordering item: {} in checklist: {}", itemId, checklistId);
        return checklistService.reorderItem(checklistId, itemId, request.position());
    }

    @DELETE
    @Path("/{checklistId}/items/{itemId}")
    @Operation(summary = "Delete a checklist item")
    @APIResponse(responseCode = "204", description = "Item deleted successfully")
    public Response deleteItem(
            @Parameter(description = "Checklist ID") @PathParam("checklistId") UUID checklistId,
            @Parameter(description = "Item ID") @PathParam("itemId") UUID itemId) {
        log.debug("Deleting item: {} in checklist: {}", itemId, checklistId);
        checklistService.deleteItem(checklistId, itemId);
        return Response.noContent().build();
    }
}
