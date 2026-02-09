package com.axis.notification.exception;

import com.axis.common.dto.ApiError;
import com.axis.common.exception.BusinessException;
import com.axis.common.exception.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    public Response mapValidationException(ConstraintViolationException ex) {
        log.warn("Validation failed: {}", ex.getMessage());

        List<ApiError.FieldError> fieldErrors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            Object invalidValue = violation.getInvalidValue();

            fieldErrors.add(ApiError.FieldError.builder()
                .field(fieldName)
                .message(message)
                .rejectedValue(invalidValue)
                .build());
        }

        ApiError error = ApiError.builder()
            .timestamp(LocalDateTime.now())
            .status(400)
            .error("Validation Failed")
            .message("Input validation failed")
            .fieldErrors(fieldErrors)
            .build();

        return Response.status(400).entity(error).build();
    }

    @ServerExceptionMapper
    public Response mapGenericException(Exception ex) {
        log.error("Unexpected error", ex);
        ApiError error = ApiError.builder()
            .timestamp(LocalDateTime.now())
            .status(500)
            .error("Internal Server Error")
            .message("An unexpected error occurred")
            .build();
        return Response.status(500).entity(error).build();
    }
}
