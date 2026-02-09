package com.axis.media.exception;

import com.axis.common.dto.ApiError;
import com.axis.common.exception.BusinessException;
import com.axis.common.exception.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;

@ApplicationScoped
@Slf4j
public class ExceptionMappers {

    @ServerExceptionMapper
    public Response mapResourceNotFound(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        ApiError error = ApiError.builder()
            .timestamp(LocalDateTime.now())
            .status(404)
            .error("Not Found")
            .message(ex.getMessage())
            .build();
        return Response.status(404).entity(error).build();
    }

    @ServerExceptionMapper
    public Response mapBusinessException(BusinessException ex) {
        log.error("Business exception: {}", ex.getMessage());
        ApiError error = ApiError.builder()
            .timestamp(LocalDateTime.now())
            .status(ex.getStatus().getStatusCode())
            .error(ex.getStatus().getReasonPhrase())
            .message(ex.getMessage())
            .build();
        return Response.status(ex.getStatus()).entity(error).build();
    }

    @ServerExceptionMapper
    public Response mapGenericException(Exception ex) {
        log.error("Unexpected error", ex);
        ApiError error = ApiError.builder()
            .timestamp(LocalDateTime.now())
            .status(500)
            .error("Internal Server Error")
            .message(ex.getMessage())
            .build();
        return Response.status(500).entity(error).build();
    }
}
