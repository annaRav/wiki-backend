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
@Path("/api/goals/{goalId}/checklists")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RunOnVirtualThread
@Tag(name = "Checklists", description = "Checklist and checklist item management within goals")
public class ChecklistController {

    @Inject
    ChecklistService checklistService;

    // ─── Checklist endpoints ─────────────────────────────────────────────────

    @POST
    @Operation(summary = "Create a checklist", description = "Creates a new checklist within the specified goal")
    @APIResponses({
        @APIResponse(responseCode = "201", description = "Checklist created successfully"),
        @APIResponse(responseCode = "400", description = "Invalid request data"),
        @APIResponse(responseCode = "404", description = "Goal not found")
    })
    public Response createChecklist(
            @Parameter(description = "Goal ID") @PathParam("goalId") UUID goalId,
            @Valid ChecklistRequest request) {
        log.debug("Creating checklist in goal: {}", goalId);
        ChecklistResponse response = checklistService.createChecklist(goalId, request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @Operation(summary = "Get all checklists", description = "Retrieves all checklists for the specified goal, ordered by position")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Checklists retrieved successfully"),
        @APIResponse(responseCode = "404", description = "Goal not found")
    })
    public List<ChecklistResponse> findAllChecklists(
            @Parameter(description = "Goal ID") @PathParam("goalId") UUID goalId) {
        log.debug("Finding all checklists in goal: {}", goalId);
        return checklistService.findAllChecklists(goalId);
    }

    @PATCH
    @Path("/{checklistId}")
    @Operation(summary = "Update a checklist", description = "Partially updates a checklist (only provided fields are changed)")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Checklist updated successfully"),
        @APIResponse(responseCode = "404", description = "Goal or checklist not found")
    })
    public ChecklistResponse patchChecklist(
            @Parameter(description = "Goal ID") @PathParam("goalId") UUID goalId,
            @Parameter(description = "Checklist ID") @PathParam("checklistId") UUID checklistId,
            ChecklistRequest request) {
        log.debug("Patching checklist: {} in goal: {}", checklistId, goalId);
        return checklistService.patchChecklist(goalId, checklistId, request);
    }

    @DELETE
    @Path("/{checklistId}")
    @Operation(summary = "Delete a checklist", description = "Deletes a checklist and all its items")
    @APIResponses({
        @APIResponse(responseCode = "204", description = "Checklist deleted successfully"),
        @APIResponse(responseCode = "404", description = "Goal or checklist not found")
    })
    public Response deleteChecklist(
            @Parameter(description = "Goal ID") @PathParam("goalId") UUID goalId,
            @Parameter(description = "Checklist ID") @PathParam("checklistId") UUID checklistId) {
        log.debug("Deleting checklist: {} in goal: {}", checklistId, goalId);
        checklistService.deleteChecklist(goalId, checklistId);
        return Response.noContent().build();
    }

    // ─── Checklist item endpoints ─────────────────────────────────────────────

    @POST
    @Path("/{checklistId}/items")
    @Operation(summary = "Create a checklist item", description = "Adds a new item to the specified checklist")
    @APIResponses({
        @APIResponse(responseCode = "201", description = "Item created successfully"),
        @APIResponse(responseCode = "400", description = "Invalid request data"),
        @APIResponse(responseCode = "404", description = "Goal or checklist not found")
    })
    public Response createItem(
            @Parameter(description = "Goal ID") @PathParam("goalId") UUID goalId,
            @Parameter(description = "Checklist ID") @PathParam("checklistId") UUID checklistId,
            @Valid ChecklistItemRequest request) {
        log.debug("Creating item in checklist: {} in goal: {}", checklistId, goalId);
        ChecklistItemResponse response = checklistService.createItem(goalId, checklistId, request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PATCH
    @Path("/{checklistId}/items/{itemId}")
    @Operation(summary = "Update a checklist item", description = "Partially updates an item (title and/or completed status)")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Item updated successfully"),
        @APIResponse(responseCode = "404", description = "Goal, checklist, or item not found")
    })
    public ChecklistItemResponse patchItem(
            @Parameter(description = "Goal ID") @PathParam("goalId") UUID goalId,
            @Parameter(description = "Checklist ID") @PathParam("checklistId") UUID checklistId,
            @Parameter(description = "Item ID") @PathParam("itemId") UUID itemId,
            ChecklistItemRequest request) {
        log.debug("Patching item: {} in checklist: {}", itemId, checklistId);
        return checklistService.patchItem(goalId, checklistId, itemId, request);
    }

    @PATCH
    @Path("/{checklistId}/items/{itemId}/position")
    @Operation(summary = "Reorder a checklist item", description = "Moves an item to a new position within the checklist")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Item reordered successfully"),
        @APIResponse(responseCode = "400", description = "Invalid position"),
        @APIResponse(responseCode = "404", description = "Goal, checklist, or item not found")
    })
    public ChecklistItemResponse reorderItem(
            @Parameter(description = "Goal ID") @PathParam("goalId") UUID goalId,
            @Parameter(description = "Checklist ID") @PathParam("checklistId") UUID checklistId,
            @Parameter(description = "Item ID") @PathParam("itemId") UUID itemId,
            @Valid ReorderItemRequest request) {
        log.debug("Reordering item: {} to position: {} in checklist: {}", itemId, request.position(), checklistId);
        return checklistService.reorderItem(goalId, checklistId, itemId, request.position());
    }

    @DELETE
    @Path("/{checklistId}/items/{itemId}")
    @Operation(summary = "Delete a checklist item", description = "Deletes a specific item from a checklist")
    @APIResponses({
        @APIResponse(responseCode = "204", description = "Item deleted successfully"),
        @APIResponse(responseCode = "404", description = "Goal, checklist, or item not found")
    })
    public Response deleteItem(
            @Parameter(description = "Goal ID") @PathParam("goalId") UUID goalId,
            @Parameter(description = "Checklist ID") @PathParam("checklistId") UUID checklistId,
            @Parameter(description = "Item ID") @PathParam("itemId") UUID itemId) {
        log.debug("Deleting item: {} in checklist: {}", itemId, checklistId);
        checklistService.deleteItem(goalId, checklistId, itemId);
        return Response.noContent().build();
    }
}
