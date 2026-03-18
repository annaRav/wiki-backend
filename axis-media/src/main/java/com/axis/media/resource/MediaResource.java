package com.axis.media.resource;

import com.axis.common.security.SecurityUtils;
import com.axis.media.dto.MediaFileResponse;
import com.axis.media.service.MediaFileService;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

@Slf4j
@Path("/api/media/files")
@Produces(MediaType.APPLICATION_JSON)
@RunOnVirtualThread
@Tag(name = "Media Files", description = "Media file management API for uploading and retrieving files")
public class MediaResource {

    @Inject
    MediaFileService mediaFileService;

    @Inject
    SecurityUtils securityUtils;

    public static class UploadForm {
        @RestForm("file")
        public FileUpload file;

        @RestForm("filename")
        @PartType(MediaType.TEXT_PLAIN)
        public String filename;
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(summary = "Upload a file", description = "Upload a media file for the authenticated user")
    @APIResponses(value = {
        @APIResponse(responseCode = "201", description = "File uploaded successfully"),
        @APIResponse(responseCode = "400", description = "Invalid request"),
        @APIResponse(responseCode = "401", description = "User not authenticated")
    })
    public Response upload(UploadForm form) {
        UUID ownerId = getCurrentUserId();
        String originalFilename = form.filename != null ? form.filename : form.file.fileName();
        String mimeType = form.file.contentType() != null ? form.file.contentType() : MediaType.APPLICATION_OCTET_STREAM;

        log.debug("Uploading file: {} for owner: {}", originalFilename, ownerId);
        try (InputStream inputStream = Files.newInputStream(form.file.uploadedFile())) {
            MediaFileResponse response = mediaFileService.upload(inputStream, originalFilename, mimeType, ownerId);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (IOException e) {
            log.error("Failed to read uploaded file", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Operation(summary = "List files", description = "List all files for the authenticated user (paginated)")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Files retrieved successfully"),
        @APIResponse(responseCode = "401", description = "User not authenticated")
    })
    public List<MediaFileResponse> list(
        @QueryParam("page") @DefaultValue("0") int page,
        @QueryParam("size") @DefaultValue("20") int size) {
        UUID ownerId = getCurrentUserId();
        log.debug("Listing files for owner: {}", ownerId);
        return mediaFileService.listByOwner(ownerId, page, size);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Operation(summary = "Download a file", description = "Download a file by ID")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "File downloaded successfully"),
        @APIResponse(responseCode = "401", description = "User not authenticated"),
        @APIResponse(responseCode = "404", description = "File not found")
    })
    public Response download(@Parameter(description = "File ID") @PathParam("id") UUID id) {
        UUID ownerId = getCurrentUserId();
        log.debug("Downloading file: {} for owner: {}", id, ownerId);
        File file = mediaFileService.download(id, ownerId);
        return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM).build();
    }

    @GET
    @Path("/{id}/metadata")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get file metadata", description = "Get metadata for a file by ID")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Metadata retrieved successfully"),
        @APIResponse(responseCode = "401", description = "User not authenticated"),
        @APIResponse(responseCode = "404", description = "File not found")
    })
    public MediaFileResponse getMetadata(@Parameter(description = "File ID") @PathParam("id") UUID id) {
        UUID ownerId = getCurrentUserId();
        log.debug("Getting metadata for file: {} for owner: {}", id, ownerId);
        return mediaFileService.getMetadata(id, ownerId);
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a file", description = "Delete a file by ID")
    @APIResponses(value = {
        @APIResponse(responseCode = "204", description = "File deleted successfully"),
        @APIResponse(responseCode = "401", description = "User not authenticated"),
        @APIResponse(responseCode = "404", description = "File not found")
    })
    public Response delete(@Parameter(description = "File ID") @PathParam("id") UUID id) {
        UUID ownerId = getCurrentUserId();
        log.debug("Deleting file: {} for owner: {}", id, ownerId);
        mediaFileService.delete(id, ownerId);
        return Response.noContent().build();
    }

    private UUID getCurrentUserId() {
        return securityUtils.getCurrentUserIdAsUUID()
            .orElseThrow(() -> new IllegalStateException("User is not authenticated"));
    }
}
