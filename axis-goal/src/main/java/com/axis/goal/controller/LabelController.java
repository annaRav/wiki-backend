package com.axis.goal.controller;

import com.axis.goal.model.dto.LabelRequest;
import com.axis.goal.model.dto.LabelResponse;
import com.axis.goal.service.LabelService;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@Slf4j
@Path("/api/labels")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RunOnVirtualThread
@Tag(name = "Labels")
public class LabelController {

    @Inject
    LabelService labelService;

    @POST
    @Operation(summary = "Create a new label")
    @APIResponse(responseCode = "201", description = "Label created successfully")
    public Response create(@Valid LabelRequest request) {
        LabelResponse response = labelService.create(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @Operation(summary = "List all labels for the authenticated user")
    @APIResponse(responseCode = "200", description = "List of labels")
    public List<LabelResponse> findAll() {
        return labelService.findAll();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get a label by ID")
    @APIResponse(responseCode = "200", description = "Label found")
    @APIResponse(responseCode = "404", description = "Label not found")
    public LabelResponse findById(@PathParam("id") UUID id) {
        return labelService.findById(id);
    }

    @PATCH
    @Path("/{id}")
    @Operation(summary = "Partially update a label")
    @APIResponse(responseCode = "200", description = "Label updated successfully")
    @APIResponse(responseCode = "404", description = "Label not found")
    public LabelResponse patch(@PathParam("id") UUID id, LabelRequest request) {
        return labelService.patch(id, request);
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a label")
    @APIResponse(responseCode = "204", description = "Label deleted successfully")
    @APIResponse(responseCode = "404", description = "Label not found")
    public Response delete(@PathParam("id") UUID id) {
        labelService.delete(id);
        return Response.noContent().build();
    }
}