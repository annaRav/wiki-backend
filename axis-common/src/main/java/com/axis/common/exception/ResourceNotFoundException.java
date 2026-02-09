package com.axis.common.exception;

import jakarta.ws.rs.core.Response;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super(message, Response.Status.NOT_FOUND);
    }

    public ResourceNotFoundException(String resourceType, Object id) {
        super(String.format("%s with id '%s' not found", resourceType, id), Response.Status.NOT_FOUND);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, Response.Status.NOT_FOUND, cause);
    }
}
