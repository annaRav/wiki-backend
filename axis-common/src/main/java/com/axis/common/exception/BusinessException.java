package com.axis.common.exception;

import jakarta.ws.rs.core.Response;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final Response.Status status;
    private final String errorCode;

    public BusinessException(String message) {
        this(message, Response.Status.BAD_REQUEST);
    }

    public BusinessException(String message, Response.Status status) {
        this(message, status, (String) null);
    }

    public BusinessException(String message, Response.Status status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public BusinessException(String message, Throwable cause) {
        this(message, Response.Status.BAD_REQUEST, cause);
    }

    public BusinessException(String message, Response.Status status, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.errorCode = null;
    }
}
