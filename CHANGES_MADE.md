# Changes Made During Cleanup

## Docker & Infrastructure Changes

### docker-compose.yml - PostgreSQL 18 Volume Fix
- **Changed PostgreSQL volume mount from `/var/lib/postgresql/data` to `/var/lib/postgresql`** - PostgreSQL 18+ images require the volume to be mounted at `/var/lib/postgresql` (not `/var/lib/postgresql/data`). This allows the database to manage version-specific subdirectories and enables `pg_upgrade --link` without mount point boundary issues. See https://github.com/docker-library/postgres/pull/1259 for details.
- **Note**: If you have existing data from an older setup, you'll need to run `docker-compose down -v` to remove the old volume before starting with the new configuration.

## Configuration Changes

### application.yaml
- **Removed `spring.main.allow-bean-definition-overriding`** - This was a workaround that hides potential bean conflicts. Proper configuration should not require this.
- **Changed `server.port` syntax** to proper YAML format (`server:\n  port: 8080`)
- **Added `server.shutdown: graceful`** - Enables graceful shutdown for proper request completion before stopping.
- **Added `spring.threads.virtual.enabled: true`** - Enables Java 21+ virtual threads for better I/O performance with minimal code changes.
- **Changed `spring.jpa.hibernate.ddl-auto` from `update` to `validate`** - In production, schema changes should be managed by Flyway, not Hibernate. `validate` ensures the schema matches entities without making changes.
- **Added `spring.jpa.open-in-view: false`** - Disabled OSIV (Open Session in View) anti-pattern that can cause N+1 queries and lazy loading issues outside transactions.
- **Added `spring.jpa.properties.hibernate.jdbc.time_zone: UTC`** - Ensures consistent timezone handling across environments.
- **Added `spring.flyway.validate-on-migrate: true`** - Validates checksums of migrations to detect tampering.
- **Removed `spring.datasource.driver-class-name`** - Spring Boot auto-detects the driver from the JDBC URL.
- **Added management endpoints configuration** - Exposed health, info, and metrics endpoints with proper security.
- **Added logging configuration** - Proper log levels and patterns for production.

### application-dev.yaml
- **Re-enabled `spring.jpa.hibernate.ddl-auto: update`** - For development, auto-update is convenient.
- **Added `spring.jpa.properties.hibernate.format_sql: true`** - Formats SQL output for better readability during development.
- **Added Swagger UI explicit enable** - Ensures Swagger is available in dev.
- **Added debug logging for security and application** - Helps with development troubleshooting.

### New: application-test.yaml
- **Created dedicated test profile** - Separates test configuration from other environments.

## Security Changes

### SecurityConfig.java
- **Removed `@Profile("dev")` restriction** - Security should be active in all environments, not just dev.
- **Added `@EnableMethodSecurity`** - Enables method-level security annotations like `@PreAuthorize`.
- **Removed manual `JwtDecoder` bean** - Spring Boot auto-configures this from properties, reducing boilerplate.
- **Added Swagger endpoints to permitAll** - Allows API documentation access without authentication.
- **Added `/actuator/health/**` pattern** - Properly exposes all health sub-endpoints for Kubernetes probes.
- **Used `Customizer.withDefaults()`** - Modern Spring Security pattern for JWT configuration.

### New: GlobalExceptionHandler.java
- **Created centralized exception handling** - Provides consistent error responses across all endpoints.
- **Added `ErrorResponse` record** - Structured error response with code, message, details, and timestamp.
- **Added `ObjectOptimisticLockingFailureException` handler** - Returns 409 Conflict when concurrent modifications occur.
- **Added validation exception handler** - Returns detailed field-level validation errors.
- **Added generic exception handler** - Catches unexpected errors and logs them properly.

## Entity Changes

### Recipe.java
- **Added `@Version` field** - Enables optimistic locking to prevent lost updates in concurrent scenarios.
- **Added `createdAt` and `updatedAt` audit fields** - Tracks when records were created and modified.
- **Added `@PrePersist` and `@PreUpdate` hooks** - Automatically manages audit timestamps.
- **Changed `ingredients` and `instructions` to `columnDefinition = "TEXT"`** - Better PostgreSQL compatibility for large text fields.

## DTO Changes

### RecipeDTO.java
- **Changed validation annotations** - Used `@NotBlank` instead of `@NotNull` for strings (catches empty strings).
- **Added `@Positive` for servings** - Ensures servings is a positive number.
- **Added custom validation messages** - More descriptive error messages for users.
- **Added `createdAt` and `updatedAt` fields** - Exposes audit information in API responses.
- **Changed `toEntity(dto, UUID)` to `toEntity(dto, Recipe)`** - Properly preserves version and timestamps when updating.

### RecipePatchDTO.java
- **Added version and createdAt preservation** - Ensures patch operations work with optimistic locking.

## Controller Changes

### RecipeController.java
- **Removed `@RequiredArgsConstructor`** - Used explicit constructor for better clarity and compatibility.
- **Added SLF4J logging** - Proper logging for operations instead of silent execution.
- **Fixed URI in POST response** - Added missing `/` between base path and ID.
- **Changed DELETE response from 200 to 204 No Content** - REST best practice for successful delete operations.

## Application Changes

### RecipesApplication.java
- **Replaced `System.out.println` with SLF4J logging** - Proper logging framework usage.
- **Changed `@Autowired` field injection to constructor injection** - Best practice for testability and immutability.

## Database Changes

### V1__create-recipes-table.sql
- **Removed Liquibase comments** - This is a Flyway migration, not Liquibase. Wrong tool comments were confusing.

### New: V2__add-version-and-audit-columns.sql
- **Added `version` column** - Supports optimistic locking.
- **Added `created_at` and `updated_at` columns** - Supports audit trail.
- **Changed `ingredients` and `instructions` to TEXT** - Better for large content.
- **Added indexes** - Improves query performance on commonly filtered columns.

## Docker Changes

### Dockerfile
- **Added HEALTHCHECK instruction** - Enables container orchestrators (Kubernetes, Docker Swarm) to monitor container health.

### docker-compose.yml
- **Added healthchecks to all services** - Ensures services are actually ready, not just started.
- **Changed `depends_on` to use conditions** - Services wait for dependencies to be healthy, not just running.
- **Fixed postgres volume path** - Changed to `/var/lib/postgresql/data` (correct path).
- **Added environment variables to app service** - Makes datasource configuration explicit.

## Build & DevOps Changes

### Makefile
- **Added `.PHONY` declarations** - Proper Make targets that don't represent files.
- **Added `run-detached` command** - Run in background mode.
- **Added `stop` command** - Stop all containers.
- **Added `test` command** - Run all tests (unit + integration).
- **Added `clean` command** - Clean build artifacts.
- **Added `build` command** - Build without integration tests.

### .gitignore
- **Added secrets patterns** - `*.env`, `token.txt`, `*.pem`, `*.key`, etc.
- **Added logs patterns** - `*.log`, `logs/`
- **Added OS files** - `.DS_Store`, `Thumbs.db`

### .dockerignore  
- **Reorganized with sections** - Better organization and comments.
- **Added secrets patterns** - Prevents sensitive files from being included in Docker images.
- **Added test directories** - Not needed in production images.
- **Added scripts directory** - Development scripts shouldn't be in containers.

## Test Changes

### RecipeControllerTest.java
- **Fixed delete test assertion** - Changed expected status from OK (200) to NO_CONTENT (204).

### RecipeControllerIT.java
- **Fixed delete test assertion** - Changed expected status from `isOk()` to `isNoContent()`.

### New: src/integration-test/resources/application-test.yaml
- **Created test resources directory with config** - Proper test configuration.
