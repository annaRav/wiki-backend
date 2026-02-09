package com.axis.media.controller;

import io.smallrye.common.annotation.Blocking;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.Map;

@Path("/api/media")
@Tag(name = "Health Check", description = "Health check endpoints for axis-media service")
public class HealthCheckController {

    @GET
    @Path("/health")
    @Produces(MediaType.APPLICATION_JSON)
    @Blocking
    @Operation(
        summary = "Health check endpoint",
        description = "Returns the health status of the axis-media service"
    )
    @APIResponse(responseCode = "200", description = "Service is healthy")
    public Response health() {
        return Response.ok(Map.of(
            "service", "axis-media",
            "status", "UP",
            "timestamp", LocalDateTime.now().toString(),
            "database", "MongoDB",
            "version", "1.0.0-SNAPSHOT"
        )).build();
    }

    @GET
    @Path("/ping")
    @Produces(MediaType.TEXT_PLAIN)
    @Blocking
    @Operation(
        summary = "Ping endpoint",
        description = "Simple ping endpoint to check if service is responding"
    )
    @APIResponse(responseCode = "200", description = "Service is responding")
    public String ping() {
        return "pong";
    }
}
