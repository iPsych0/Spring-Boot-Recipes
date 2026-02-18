# Copilot Instructions for Recipes Application

## Project Overview

This is a **production-grade Spring Boot 4 application** for managing recipes. It uses Java 25, PostgreSQL, Flyway migrations, JWT authentication via Keycloak, and follows enterprise-grade quality standards while maintaining simplicity (KISS principle).

---

## Technology Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 25 | Language runtime (use modern features: records, pattern matching, virtual threads where beneficial) |
| Spring Boot | 4.x | Application framework |
| PostgreSQL | 18.x | Primary database |
| Flyway | 11.x | Database migrations |
| Keycloak | 26.x | OAuth2/JWT identity provider |
| Testcontainers | 1.21.x | Integration testing with real containers |
| JUnit | 6.x (prefer) / 5.x (fallback) | Unit and integration testing |
| Mockito | Latest | Mocking framework |
| Docker | Latest | Containerization |

---

## Core Principles

### 1. KISS (Keep It Simple, Stupid)
- **DO NOT** create overly complicated solutions or optimize prematurely
- **DO NOT** introduce patterns like Hexagonal Architecture unless explicitly requested
- **FAVOR** readability over performance—this application does not need to handle thousands of concurrent requests
- **RECOMMEND** performance improvements only when the current solution is unacceptable for scaling

### 2. Enterprise-Grade Robustness (Not Complexity)
- Focus on reliability, security, and maintainability
- Use proper error handling and validation
- Implement comprehensive logging
- Ensure proper transaction management

### 3. Security First
- **NEVER** commit secrets (JWT keys, passwords, API keys) to git
- **ALWAYS** use environment variables or external secret management for sensitive data
- **ALWAYS** update `.gitignore` and `.dockerignore` when adding files that contain secrets
- Use Spring Security with OAuth2/JWT for authentication
- Follow the principle of least privilege

---

## Build Configuration

### Gradle (Kotlin DSL Only)
- **ALWAYS** use `build.gradle.kts` (Kotlin DSL), never Groovy
- Keep dependencies up to date with stable versions
- Use Spring Boot's dependency management for version alignment

```kotlin
// Example dependency declaration
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
}
```

### Adding Dependencies
When adding new dependencies:
1. Check if Spring Boot manages the version (omit version if so)
2. Use the appropriate scope (`implementation`, `testImplementation`, `runtimeOnly`)
3. Prefer official Spring Boot starters when available

---

## Spring Boot 4 Configuration

### Application Properties
- Use YAML format (`application.yaml`)
- Create profile-specific files: `application-{profile}.yaml`
- **ALWAYS** use Spring Boot 4 property names (avoid deprecated Spring Boot 3 properties)

### Required Profiles
| Profile | Purpose | File |
|---------|---------|------|
| `dev` | Local development with Docker Compose | `application-dev.yaml` |
| `test` | Automated testing | `application-test.yaml` |
| `staging` | Pre-production environment | `application-staging.yaml` |
| `prod` | Production environment | `application-prod.yaml` |

### Environment Variables
- Use `${ENV_VAR:default}` syntax for externalized configuration
- **NEVER** hardcode secrets in property files
- Document required environment variables in README.md

```yaml
# Correct: externalized configuration
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
```

### Spring Boot 4 Specific
- Use `spring.threads.virtual.enabled=true` for virtual threads when I/O bound
- Prefer `spring.docker.compose.enabled=true` for local development
- Use structured logging with `spring.logging.structured.format`
- Leverage the new observability features with Micrometer

---

## Java 25 Best Practices

### Modern Java Features to Use
```java
// ✅ Use records for DTOs and value objects
public record RecipeRequest(
    @NotBlank String name,
    boolean vegetarian,
    @Positive int servings,
    @NotBlank String ingredients,
    @NotBlank String instructions
) {}

// ✅ Use pattern matching for instanceof
if (obj instanceof Recipe recipe) {
    return recipe.getName();
}

// ✅ Use switch expressions
String category = switch (recipe.getServings()) {
    case 1, 2 -> "small";
    case 3, 4 -> "medium";
    default -> "large";
};

// ✅ Use text blocks for multi-line strings
String query = """
    SELECT r FROM Recipe r
    WHERE r.vegetarian = :vegetarian
    """;

// ✅ Use sealed classes for domain modeling when appropriate
public sealed interface RecipeEvent permits RecipeCreated, RecipeUpdated, RecipeDeleted {}
```

### Code Style
- Use `var` for local variables when the type is obvious
- Prefer immutability—use `final` fields and records
- Use Optional for nullable return types, never for parameters
- Follow standard Java naming conventions

---

## Testing Guidelines

### Test Framework Hierarchy
1. **JUnit 6** (preferred) - Use when available
2. **JUnit 5** (fallback) - Use Jupiter annotations
3. **Mockito** - For mocking dependencies
4. **MockMvc** - For controller/web layer tests
5. **Testcontainers** - For integration tests requiring real infrastructure

### Test Structure
```
src/
├── test/java/                    # Unit tests
│   └── com/abn/recipes/
│       ├── controllers/          # Controller unit tests (MockMvc + Mockito)
│       ├── services/             # Service unit tests (Mockito)
│       └── domain/               # Domain logic tests
└── integration-test/java/        # Integration tests
    └── com/abn/recipes/
        ├── controllers/          # Full stack API tests (Testcontainers)
        └── repositories/         # Repository tests (Testcontainers)
```

### Unit Tests (MockMvc + Mockito)
```java
@WebMvcTest(RecipeController.class)
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecipeService recipeService;

    @Test
    @WithMockUser
    void shouldReturnRecipeWhenFound() throws Exception {
        // Given
        var recipe = new Recipe(1L, "Pasta", false, 2, "ingredients", "instructions");
        when(recipeService.findById(1L)).thenReturn(Optional.of(recipe));

        // When/Then
        mockMvc.perform(get("/api/v1/recipes/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Pasta"));
    }
}
```

### Integration Tests (Testcontainers)
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class RecipeControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18.1");

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldCreateRecipe() {
        // Full integration test with real database
    }
}
```

### Test Naming Convention
- Use descriptive method names: `should[Action]When[Condition]`
- Group related tests with `@Nested` classes
- Use `@DisplayName` for complex test scenarios

### Running Tests
```bash
# Unit tests only
./gradlew test

# Integration tests only
./gradlew integrationTest

# All tests
./gradlew check
```

---

## Database & Flyway

### Migration Files
- Location: `src/main/resources/db/migration/`
- Naming: `V{version}__{description}.sql` (double underscore)
- **NEVER** modify existing migrations in production
- **ALWAYS** create new migrations for schema changes

```sql
-- V2__add-recipe-category.sql
ALTER TABLE recipes ADD COLUMN category VARCHAR(50);
CREATE INDEX idx_recipes_category ON recipes(category);
```

### Flyway Best Practices
- Use versioned migrations (`V1__`, `V2__`) for schema changes
- Use repeatable migrations (`R__`) for views, procedures, reference data
- Test migrations with Testcontainers before deploying
- Include rollback strategy in migration comments when complex

### PostgreSQL Best Practices
- Use appropriate data types (TEXT over VARCHAR when length is unknown)
- Create indexes for frequently queried columns
- Use foreign keys for referential integrity
- Consider using JSONB for flexible schema requirements

---

## Docker Best Practices

### Dockerfile
```dockerfile
# Multi-stage build for smaller images
FROM gradle:9.2.1-jdk25 AS build
WORKDIR /app
COPY gradlew gradle build.gradle.kts settings.gradle.kts ./
COPY gradle gradle
RUN ./gradlew dependencies --no-daemon
COPY src src
RUN ./gradlew clean build -x integrationTest --no-daemon

# Minimal runtime image
FROM eclipse-temurin:25-jre-alpine AS runner
WORKDIR /app

# Security: non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=build --chown=appuser:appgroup /app/build/libs/*.jar app.jar
USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose
- Use health checks and `depends_on` with conditions
- Never hardcode secrets—use environment variables or Docker secrets
- Use named volumes for persistent data
- Pin image versions for reproducibility

### .dockerignore Must Include
```
.idea/
.vscode/
.git/
.gitignore
*.md
Makefile
build/
.gradle/
*.log
*.env
*.env.*
secrets/
token.txt
```

---

## Security Configuration

### JWT/OAuth2 Resource Server
```java
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)  // Stateless API
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
            .build();
    }
}
```

### Secrets Management
- **NEVER** commit secrets to version control
- Use environment variables for local development
- Use a secret manager (Vault, AWS Secrets Manager) in production
- Rotate secrets regularly

---

## API Design

### REST Conventions
- Use plural nouns for resources: `/api/v1/recipes`
- Use HTTP methods correctly: GET (read), POST (create), PUT (update), DELETE (remove)
- Return appropriate status codes: 200, 201, 204, 400, 401, 403, 404, 500
- Version APIs in the URL: `/api/v1/...`

### Request/Response DTOs
- Use Java records for immutability
- Apply validation annotations
- Separate request and response DTOs when needed

### Error Handling
```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecipeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(RecipeNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .toList();
        return ResponseEntity.badRequest()
            .body(new ErrorResponse("VALIDATION_ERROR", String.join(", ", errors)));
    }
}
```

---

## Documentation Updates

### When to Update ARCHITECTURE.md
- Major architectural decisions
- New technology additions
- Significant pattern changes
- Security model changes

### When to Update README.md
- New configuration options
- Environment variable changes
- Setup instruction changes
- New API endpoints or features

### DO NOT Create
- Separate markdown files for each change
- Changelog files (use git history)
- Excessive inline documentation

---

## Code Review Checklist

Before completing any task, verify:

- [ ] No secrets committed to git
- [ ] `.gitignore` and `.dockerignore` updated if needed
- [ ] Tests added/updated (unit + integration where appropriate)
- [ ] Validation annotations on DTOs
- [ ] Proper error handling
- [ ] Logging for important operations
- [ ] Spring Boot 4 properties used (not deprecated ones)
- [ ] Flyway migrations are additive (no modifications to existing)
- [ ] README.md updated for configuration changes
- [ ] ARCHITECTURE.md updated for major decisions

---

## Common Patterns

### Service Layer
```java
@Service
@Transactional(readOnly = true)
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public Optional<Recipe> findById(Long id) {
        return recipeRepository.findById(id);
    }

    @Transactional
    public Recipe create(RecipeRequest request) {
        var recipe = new Recipe(request);
        return recipeRepository.save(recipe);
    }
}
```

### Repository Layer
```java
public interface RecipeRepository extends JpaRepository<Recipe, Long>, JpaSpecificationExecutor<Recipe> {
    
    // Use Spring Data query methods for simple queries
    List<Recipe> findByVegetarian(boolean vegetarian);
    
    // Use @Query for complex queries
    @Query("SELECT r FROM Recipe r WHERE r.servings >= :minServings")
    List<Recipe> findByMinimumServings(@Param("minServings") int minServings);
}
```

---

## Improvement Recommendations

When reviewing or modifying code, **proactively suggest improvements** if they are:
1. Low effort to implement
2. High impact on code quality, security, or maintainability
3. Aligned with current best practices

Always explain **why** the improvement is beneficial.

Example:
> "I noticed the entity doesn't have `@Version` for optimistic locking. Adding it prevents lost updates in concurrent scenarios and is a one-line change."

---

## Quick Reference

| Task | Command |
|------|---------|
| Run locally | `make run` or `docker-compose up --build` |
| Unit tests | `make unit-test` or `./gradlew test` |
| Integration tests | `make integration-test` or `./gradlew integrationTest` |
| All tests | `./gradlew check` |
| Setup Keycloak | `./scripts/get-token.sh setup` |
| Get JWT token | `./scripts/get-token.sh` |
