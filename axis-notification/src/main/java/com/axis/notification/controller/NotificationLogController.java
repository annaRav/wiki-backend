package com.axis.notification.controller;

import com.axis.notification.model.dto.PageResponse;
import com.axis.notification.model.dto.NotificationLogRequest;
import com.axis.notification.model.dto.NotificationLogResponse;
import com.axis.notification.model.entity.NotificationLog;
import com.axis.notification.service.NotificationLogService;
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
@Path("/api/notifications/logs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RunOnVirtualThread
@Tag(name = "Notification Logs", description = "Endpoints for managing user notification logs")
public class NotificationLogController {

    @Inject
    NotificationLogService service;

    @POST
    @Operation(summary = "Create notification log", description = "Creates a new notification log entry for the current user")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "Notification created successfully"),
            @APIResponse(responseCode = "400", description = "Invalid request data"),
            @APIResponse(responseCode = "401", description = "User not authenticated")
    })
    public Response create(@Valid NotificationLogRequest request) {
        log.info("Received request to create notification log with channel: {}", request.channel());
        NotificationLogResponse response = service.create(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get notification by ID", description = "Retrieves a notification log entry by its ID")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Notification found"),
            @APIResponse(responseCode = "403", description = "Access denied to notification"),
            @APIResponse(responseCode = "404", description = "Notification not found")
    })
    public NotificationLogResponse findById(
            @Parameter(description = "Notification ID") @PathParam("id") UUID id) {
        log.info("Received request to find notification with id: {}", id);
        return service.findById(id);
    }

    @GET
    @Operation(summary = "List user notifications", description = "Retrieves all notifications for the current user with pagination")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Notifications retrieved successfully"),
            @APIResponse(responseCode = "401", description = "User not authenticated")
    })
    public PageResponse<NotificationLogResponse> findAll(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
            @QueryParam("sortDirection") @DefaultValue("desc") String sortDirection) {
        log.info("Received request to find all notifications with pagination: page={}, size={}", page, size);
        return service.findByCurrentUser(page, size, sortBy, sortDirection);
    }

    @GET
    @Path("/status/{status}")
    @Operation(summary = "List notifications by status", description = "Retrieves notifications filtered by status for the current user")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Notifications retrieved successfully"),
            @APIResponse(responseCode = "401", description = "User not authenticated")
    })
    public PageResponse<NotificationLogResponse> findByStatus(
            @Parameter(description = "Notification status") @PathParam("status") NotificationLog.Status status,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
            @QueryParam("sortDirection") @DefaultValue("desc") String sortDirection) {
        log.info("Received request to find notifications with status: {}", status);
        return service.findByCurrentUserAndStatus(status, page, size, sortBy, sortDirection);
    }

    @GET
    @Path("/channel/{channel}")
    @Operation(summary = "List notifications by channel", description = "Retrieves notifications filtered by channel for the current user")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Notifications retrieved successfully"),
            @APIResponse(responseCode = "401", description = "User not authenticated")
    })
    public PageResponse<NotificationLogResponse> findByChannel(
            @Parameter(description = "Notification channel") @PathParam("channel") NotificationLog.Channel channel,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
            @QueryParam("sortDirection") @DefaultValue("desc") String sortDirection) {
        log.info("Received request to find notifications with channel: {}", channel);
        return service.findByCurrentUserAndChannel(channel, page, size, sortBy, sortDirection);
    }

    @GET
    @Path("/unread/count")
    @Operation(summary = "Count unread notifications", description = "Returns the count of unread notifications for the current user")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Count retrieved successfully"),
            @APIResponse(responseCode = "401", description = "User not authenticated")
    })
    public long countUnread() {
        log.info("Received request to count unread notifications");
        return service.countUnread();
    }

    @PATCH
    @Path("/{id}/status")
    @Operation(summary = "Update notification status", description = "Updates the status of a notification (e.g., mark as read)")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Status updated successfully"),
            @APIResponse(responseCode = "403", description = "Access denied to notification"),
            @APIResponse(responseCode = "404", description = "Notification not found")
    })
    public NotificationLogResponse updateStatus(
            @Parameter(description = "Notification ID") @PathParam("id") UUID id,
            @Parameter(description = "New status") @QueryParam("status") NotificationLog.Status status) {
        log.info("Received request to update notification {} status to {}", id, status);
        return service.updateStatus(id, status);
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete notification", description = "Deletes a notification by its ID")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Notification deleted successfully"),
            @APIResponse(responseCode = "403", description = "Access denied to notification"),
            @APIResponse(responseCode = "404", description = "Notification not found")
    })
    public Response deleteById(
            @Parameter(description = "Notification ID") @PathParam("id") UUID id) {
        log.info("Received request to delete notification with id: {}", id);
        service.deleteById(id);
        return Response.noContent().build();
    }

    @DELETE
    @Operation(summary = "Delete all notifications", description = "Deletes all notifications for the current user")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "All notifications deleted successfully"),
            @APIResponse(responseCode = "401", description = "User not authenticated")
    })
    public Response deleteAll() {
        log.info("Received request to delete all notifications for current user");
        service.deleteByCurrentUser();
        return Response.noContent().build();
    }
}
