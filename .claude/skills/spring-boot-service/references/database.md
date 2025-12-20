# Database Configuration

## PostgreSQL (JPA/Hibernate)

### application.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:postgres-app}:${DB_PORT:5432}/${DB_NAME:wiki_membership}
    username: ${DB_USERNAME:wiki_user}
    password: ${DB_PASSWORD:wiki_password}
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 20000
      idle-timeout: 300000
      max-lifetime: 1200000

  jpa:
    hibernate:
      ddl-auto: validate  # ALWAYS use validate, never update/create
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
        use_sql_comments: true
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true

  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
    validate-on-migrate: true
```

**Important:**
- Always use `ddl-auto: validate` to prevent schema auto-generation
- Flyway handles all schema changes via migration scripts
- HikariCP is configured for connection pooling

### Entity Example
```java
@Entity
@Table(name = "organizations", indexes = {
    @Index(name = "idx_organization_slug", columnList = "slug")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

**Key Points:**
- Use `@Getter` and `@Setter` instead of `@Data` to avoid `equals/hashCode` issues with JPA entities
- Use `@CreationTimestamp` and `@UpdateTimestamp` from Hibernate (not Spring Data annotations)
- Use `LocalDateTime` for timestamps (consistent with current codebase)
- Always use UUID primary keys with `GenerationType.UUID`
- Add indexes at entity level using `@Table(indexes = {...})`

### Repository
```java
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    Optional<Organization> findBySlug(String slug);
    boolean existsBySlug(String slug);
    List<Organization> findByActiveTrue();

    @Query("SELECT o FROM Organization o WHERE o.active = true AND o.name LIKE %:name%")
    List<Organization> searchByName(@Param("name") String name);
}
```

---

## MongoDB (Spring Data MongoDB)

### application.yml
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://${MONGO_USER:admin}:${MONGO_PASSWORD:admin}@${MONGO_HOST:localhost}:27017/${MONGO_DB:wiki_content}?authSource=admin
```

### Document Example (for future MongoDB services)
```java
@Document(collection = "pages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Page {

    @Id
    private String id;

    @Indexed(unique = true)
    private String slug;

    private String title;
    private String content;

    @Field("author_id")
    private UUID authorId;

    @Field("organization_id")
    private UUID organizationId;

    private List<String> tags = new ArrayList<>();

    @Field("is_published")
    private boolean published;

    @Field("view_count")
    private long viewCount;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
}
```

**Note:** For MongoDB, use Spring Data's `@CreatedDate` and `@LastModifiedDate`. For JPA, use Hibernate's `@CreationTimestamp` and `@UpdateTimestamp`.

### Repository
```java
public interface PageRepository extends MongoRepository<Page, String> {
    Optional<Page> findBySlug(String slug);
    List<Page> findByTagsContaining(String tag);
    
    @Query("{ 'is_published': true }")
    Page<Page> findPublished(Pageable pageable);
}
```

### Enable Auditing
```java
@Configuration
@EnableMongoAuditing
public class MongoConfig {
}
```
