# Changes NOT Made (and Why)

## Architecture & Patterns

### Did NOT introduce Hexagonal Architecture
- **Why**: The copilot instructions explicitly state to follow KISS principles and not introduce overly complicated patterns unless explicitly requested. The current layered architecture (Controller → Repository → Database) is appropriate for this application's scope.

### Did NOT add a Service layer
- **Why**: The controller currently interacts directly with the repository. While a service layer would provide better separation of concerns, the current implementation is simple and works. Adding a service layer would be a larger refactor that wasn't explicitly requested. The instructions emphasize readability over premature abstraction.

### Did NOT implement CQRS or Event Sourcing
- **Why**: These patterns add significant complexity and are overkill for a recipe management application. The instructions emphasize KISS.

## Database

### Did NOT modify existing V1 migration data structure
- **Why**: The copilot instructions explicitly state "NEVER modify existing migrations in production." Changes were made via a new V2 migration instead.

### Did NOT add full-text search indexes
- **Why**: The ARCHITECTURE.md mentions this as a potential improvement but notes it wasn't implemented due to time constraints. The current LIKE-based search is sufficient for the application's scale. This would be a performance optimization that should only be recommended "when the solution is otherwise unacceptable in terms of scaling."

### Did NOT add database connection pooling configuration
- **Why**: Spring Boot with HikariCP provides sensible defaults. Custom tuning should only be done when there are measured performance issues.

## Security

### Did NOT add rate limiting
- **Why**: While important for production APIs, rate limiting adds complexity and depends on infrastructure (Redis, etc.). The instructions emphasize not adding complexity unless needed.

### Did NOT add CORS configuration
- **Why**: No frontend is mentioned, and CORS configuration depends on deployment topology. This should be added when there's a specific frontend requirement.

### Did NOT add API key authentication alongside JWT
- **Why**: JWT via Keycloak is already implemented. Adding another auth mechanism would increase complexity without clear benefit.

### Did NOT externalize Keycloak realm configuration
- **Why**: The current setup uses a script (`get-token.sh setup`) to configure Keycloak. Automating this further would require realm export/import configuration that adds complexity.

## Testing

### Did NOT migrate to JUnit 6
- **Why**: JUnit 6 is mentioned as preferred in the instructions, but as of the knowledge cutoff, JUnit 6 is not yet released. The codebase correctly uses JUnit 5 (Jupiter) which is the current standard.

### Did NOT add test coverage reporting
- **Why**: While useful, adding JaCoCo or similar would be a build configuration change that wasn't explicitly requested.

### Did NOT add mutation testing
- **Why**: Mutation testing (e.g., PIT) adds significant build time and complexity. Good for mature projects but not essential.

### Did NOT add contract testing (Pact)
- **Why**: No downstream consumers are mentioned. Contract testing is valuable in microservice architectures with multiple consumers.

## Performance

### Did NOT add caching (Redis/Caffeine)
- **Why**: The instructions state the application "is not expected to handle thousands of concurrent requests." Caching should be added when there are measured performance issues, not prematurely.

### Did NOT add pagination to GET /recipes
- **Why**: While a good practice for large datasets, the current implementation returns all matching recipes. Pagination would be a breaking API change. This should be considered for a future version.

### Did NOT add async processing
- **Why**: All current operations are synchronous and complete quickly. Async would add complexity without clear benefit for the current use case.

## Monitoring & Observability

### Did NOT add distributed tracing (Micrometer Tracing)
- **Why**: Single service doesn't benefit much from distributed tracing. This becomes valuable in microservice architectures.

### Did NOT add custom metrics
- **Why**: Spring Boot Actuator provides default metrics. Custom business metrics should be added based on specific monitoring requirements.

### Did NOT add structured JSON logging
- **Why**: While mentioned in the copilot instructions as a Spring Boot 4 feature, the current console logging format is sufficient for development. JSON logging is typically configured per-environment in production deployments.

## Documentation

### Did NOT add OpenAPI annotations to controllers
- **Why**: Springdoc already generates documentation automatically from the code. Manual annotations would duplicate information and require maintenance.

### Did NOT create separate CHANGELOG.md
- **Why**: The copilot instructions explicitly state "DO NOT Create... Changelog files (use git history)."

### Did NOT update README.md with all changes
- **Why**: The changes made are implementation details that don't affect how users interact with the application. The copilot instructions say to update README.md for "configuration changes" that affect setup - the fundamental setup process remains the same.

### Did NOT update ARCHITECTURE.md
- **Why**: While several improvements were made, they don't constitute "major architectural decisions" or "significant pattern changes." The architecture remains a Spring MVC application with the same structure.

## Code Style

### Did NOT remove Lombok
- **Why**: While some argue against Lombok, it's already established in the codebase and provides value (less boilerplate). Removing it would be a large refactor with no functional benefit.

### Did NOT convert all classes to records
- **Why**: Records are immutable and work well for DTOs (already using them). JPA entities need to be mutable classes for Hibernate proxying and lifecycle callbacks.

### Did NOT add null-safety annotations (@NonNull, @Nullable)
- **Why**: While helpful for documentation and static analysis, this would be a codebase-wide change. Java's Optional is used where appropriate.

## Infrastructure

### Did NOT add Kubernetes manifests
- **Why**: The application uses Docker Compose for local development. Kubernetes deployment configurations depend on the target cluster and weren't requested.

### Did NOT add CI/CD pipeline configuration
- **Why**: No CI/CD system was specified (GitHub Actions, GitLab CI, Jenkins, etc.). This depends on the deployment target.

### Did NOT add Docker multi-architecture builds
- **Why**: The current Dockerfile works for the development environment. Multi-arch builds add complexity and build time.

## Dependencies

### Did NOT upgrade any dependency versions
- **Why**: All dependencies appear to be reasonably current. Version upgrades should be done deliberately with testing, not as part of a cleanup task.

### Did NOT remove the Flyway Gradle plugin
- **Why**: While the application uses Spring Boot's Flyway integration, the Gradle plugin may be used for CLI commands or CI/CD tasks.
