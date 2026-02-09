package com.axis.notification.controller;

import com.axis.notification.model.dto.PageResponse;
import com.axis.notification.model.dto.NotificationTemplateRequest;
import com.axis.notification.model.dto.NotificationTemplateResponse;
import com.axis.notification.model.entity.NotificationTemplates;
import com.axis.notification.service.NotificationTemplatesService;
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
@Path("/api/notifications/templates")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RunOnVirtualThread
@Tag(name = "Notification Templates", description = "Endpoints for managing notification templates")
public class NotificationTemplatesController {

    @Inject
    NotificationTemplatesService service;

    @POST
    @Operation(summary = "Create notification template", description = "Creates a new notification template (admin only)")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "Template created successfully"),
            @APIResponse(responseCode = "400", description = "Invalid request data"),
            @APIResponse(responseCode = "409", description = "Template with this type already exists")
    })
    public Response create(@Valid NotificationTemplateRequest request) {
        log.info("Received request to create notification template with type: {}", request.type());
        NotificationTemplateResponse response = service.create(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update notification template", description = "Updates an existing notification template")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Template updated successfully"),
            @APIResponse(responseCode = "400", description = "Invalid request data"),
            @APIResponse(responseCode = "404", description = "Template not found"),
            @APIResponse(responseCode = "409", description = "Another template with this type already exists")
    })
    public NotificationTemplateResponse update(
            @Parameter(description = "Template ID") @PathParam("id") UUID id,
            @Valid NotificationTemplateRequest request) {
        log.info("Received request to update notification template with id: {}", id);
        return service.update(id, request);
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get notification template by ID", description = "Retrieves a notification template by its ID")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Template found"),
            @APIResponse(responseCode = "404", description = "Template not found")
    })
    public NotificationTemplateResponse findById(
            @Parameter(description = "Template ID") @PathParam("id") UUID id) {
        log.info("Received request to find notification template with id: {}", id);
        return service.findById(id);
    }

    @GET
    @Path("/type/{type}")
    @Operation(summary = "Get notification template by type", description = "Retrieves a notification template by its type")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Template found"),
            @APIResponse(responseCode = "404", description = "Template not found")
    })
    public NotificationTemplateResponse findByType(
            @Parameter(description = "Template type") @PathParam("type") NotificationTemplates.Type type) {
        log.info("Received request to find notification template with type: {}", type);
        return service.findByType(type);
    }

    @GET
    @Operation(summary = "List all notification templates", description = "Retrieves all notification templates with pagination")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Templates retrieved successfully")
    })
    public PageResponse<NotificationTemplateResponse> findAll(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
            @QueryParam("sortDirection") @DefaultValue("asc") String sortDirection) {
        log.info("Received request to find all notification templates with pagination: page={}, size={}", page, size);
        return service.findAll(page, size, sortBy, sortDirection);
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete notification template", description = "Deletes a notification template by its ID")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Template deleted successfully"),
            @APIResponse(responseCode = "404", description = "Template not found")
    })
    public Response deleteById(
            @Parameter(description = "Template ID") @PathParam("id") UUID id) {
        log.info("Received request to delete notification template with id: {}", id);
        service.deleteById(id);
        return Response.noContent().build();
    }
}
