# Exception Handling

## Global Exception Handler (from wiki-common)

The platform uses a centralized exception handler in the `wiki-common` module that all microservices inherit.

```java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(extractPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusinessException(
            BusinessException ex, WebRequest request) {
        log.warn("Business exception: {}", ex.getMessage());

        HttpStatus status = ex.getStatus();
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage())
                .path(extractPath(request))
                .build();

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        log.warn("Validation failed: {}", ex.getMessage());

        List<ApiError.FieldError> fieldErrors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    String fieldName = error instanceof FieldError ?
                            ((FieldError) error).getField() : error.getObjectName();
                    String message = error.getDefaultMessage();
                    Object rejectedValue = error instanceof FieldError ?
                            ((FieldError) error).getRejectedValue() : null;

                    return ApiError.FieldError.builder()
                            .field(fieldName)
                            .message(message)
                            .rejectedValue(rejectedValue)
                            .build();
                })
                .collect(Collectors.toList());

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Input validation failed")
                .path(extractPath(request))
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler({AuthenticationException.class, AuthenticationCredentialsNotFoundException.class})
    public ResponseEntity<ApiError> handleAuthenticationException(
            Exception ex, WebRequest request) {
        log.warn("Authentication failed: {}", ex.getMessage());

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message("Authentication failed")
                .path(extractPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        log.warn("Access denied: {}", ex.getMessage());

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message("Access denied")
                .path(extractPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(
            Exception ex, WebRequest request) {
        log.error("Unexpected error occurred", ex);

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("An unexpected error occurred")
                .path(extractPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
```

## ApiError DTO (from wiki-common)

Standardized error response structure used across all microservices.

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {

    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
    private List<FieldError> fieldErrors;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String message;
        private Object rejectedValue;
    }
}
```

### Example Response

**Validation Error (400):**
```json
{
  "timestamp": "2024-12-20T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Input validation failed",
  "path": "/api/organizations",
  "fieldErrors": [
    {
      "field": "name",
      "message": "must not be blank",
      "rejectedValue": null
    },
    {
      "field": "slug",
      "message": "must match ^[a-z0-9-]+$",
      "rejectedValue": "Invalid Slug!"
    }
  ]
}
```

**Not Found Error (404):**
```json
{
  "timestamp": "2024-12-20T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Organization not found with id: 123e4567-e89b-12d3-a456-426614174000",
  "path": "/api/organizations/123e4567-e89b-12d3-a456-426614174000",
  "fieldErrors": null
}
```

## Custom Exception Pattern

### ResourceNotFoundException

Used for 404 Not Found scenarios.

```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

// Usage
throw new ResourceNotFoundException("Organization not found with id: " + id);
```

### BusinessException

Used for business logic violations with configurable HTTP status.

```java
@Getter
public class BusinessException extends RuntimeException {
    private final HttpStatus status;

    public BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public BusinessException(String message) {
        this(message, HttpStatus.BAD_REQUEST);
    }
}

// Usage
throw new BusinessException("Organization slug already exists", HttpStatus.CONFLICT);
throw new BusinessException("Invalid operation");  // Defaults to BAD_REQUEST
```

### Service-Specific Exceptions

Create domain-specific exceptions that extend base exceptions:

```java
public class OrganizationNotFoundException extends ResourceNotFoundException {
    public OrganizationNotFoundException(UUID id) {
        super("Organization not found with id: " + id);
    }

    public OrganizationNotFoundException(String slug) {
        super("Organization not found with slug: " + slug);
    }
}

public class DuplicateSlugException extends BusinessException {
    public DuplicateSlugException(String slug) {
        super("Organization with slug '" + slug + "' already exists", HttpStatus.CONFLICT);
    }
}
```

## Usage in Services

```java
@Service
@Transactional
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;

    @Override
    public OrganizationResponseDTO findById(UUID id) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new OrganizationNotFoundException(id));

        return organizationMapper.toResponseDTO(organization);
    }

    @Override
    public OrganizationResponseDTO create(OrganizationRequestDTO request) {
        // Check for duplicate slug
        if (organizationRepository.existsBySlug(request.getSlug())) {
            throw new DuplicateSlugException(request.getSlug());
        }

        Organization organization = organizationMapper.toEntity(request);
        organization = organizationRepository.save(organization);

        return organizationMapper.toResponseDTO(organization);
    }
}
```

## Important Notes

- **Automatic Handling:** All services that depend on `wiki-common` get global exception handling automatically
- **No Need to Re-declare:** Don't create your own `@RestControllerAdvice` in individual services
- **Consistent Responses:** All errors follow the same `ApiError` structure across all microservices
- **Logging:** Errors are automatically logged at appropriate levels (warn for client errors, error for server errors)
- **Security:** Generic error messages prevent information leakage in production