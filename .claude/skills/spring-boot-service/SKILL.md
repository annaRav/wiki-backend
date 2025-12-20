---
name: spring-boot-service
description: Guide for creating and modifying Spring Boot 3 microservices in Wiki Platform. Use when creating new services, adding endpoints, implementing service layer logic, configuring databases, or following project coding standards.
---

# Spring Boot Service Development

## Quick Start: New Service

1. Copy existing service as template (e.g., `wiki-user`)
2. Update `pom.xml`: artifactId, dependencies
3. Update `application.yml`: port, database config
4. Create package structure (see below)
5. Add to `docker-compose.yml` and k8s manifests

## Package Structure

```
com.wiki.{service}/
├── WikiServiceApplication.java
├── config/
│   ├── SecurityConfig.java
│   └── WebClientConfig.java
├── controller/
│   └── {Entity}Controller.java
├── service/
│   ├── {Entity}Service.java
│   └── impl/
│       └── {Entity}ServiceImpl.java
├── repository/
│   └── {Entity}Repository.java
├── model/
│   ├── entity/
│   │   └── {Entity}.java
│   └── dto/
│       ├── {Entity}Request.java
│       └── {Entity}Response.java
├── mapper/
│   └── {Entity}Mapper.java
└── exception/
    ├── GlobalExceptionHandler.java
    └── {Entity}NotFoundException.java
```

## Layer Responsibilities

**Controller** — HTTP handling only:
- Request validation (@Valid)
- Call service methods
- Return ResponseEntity

**Service** — Business logic:
- Interface + Impl pattern
- Transaction management (@Transactional)
- Cross-cutting concerns

**Repository** — Data access:
- Spring Data interfaces
- Custom queries when needed

## Code Patterns

### Controller Example
```java
@RestController
@RequestMapping("/api/pages")
@RequiredArgsConstructor
public class PageController {

    private final PageService pageService;

    @GetMapping
    public ResponseEntity<Page<PageResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(pageService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PageResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(pageService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PageResponse> create(
            @Valid @RequestBody PageRequest request) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(pageService.create(request));
    }
}
```

### Service Example
```java
public interface PageService {
    Page<PageResponse> findAll(Pageable pageable);
    PageResponse findById(String id);
    PageResponse create(PageRequest request);
}

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PageServiceImpl implements PageService {

    private final PageRepository pageRepository;
    private final PageMapper pageMapper;

    @Override
    public PageResponse findById(String id) {
        return pageRepository.findById(id)
            .map(pageMapper::toResponse)
            .orElseThrow(() -> new PageNotFoundException(id));
    }

    @Override
    @Transactional
    public PageResponse create(PageRequest request) {
        var entity = pageMapper.toEntity(request);
        var saved = pageRepository.save(entity);
        return pageMapper.toResponse(saved);
    }
}
```

## Naming Conventions

| Type | Pattern | Example |
|------|---------|---------|
| Entity | `{Name}` | `Page`, `User` |
| Request DTO | `{Name}Request` | `PageRequest` |
| Response DTO | `{Name}Response` | `PageResponse` |
| Service | `{Name}Service` | `PageService` |
| Repository | `{Name}Repository` | `PageRepository` |
| Controller | `{Name}Controller` | `PageController` |
| Exception | `{Name}NotFoundException` | `PageNotFoundException` |
| Mapper | `{Name}Mapper` | `PageMapper` |

## References

- Exception handling: See `references/exceptions.md`
- Database configs: See `references/database.md`
- Testing patterns: See `references/testing.md`
