# Architectural Decisions

### Stack

- Java 25 (leveraging the latest features and improvements)
- Spring Boot 4 (for rapid application development and ease of configuration)
- PostgreSQL (robust relational database for data persistence)
- Docker & docker-compose (for containerization and easy setup of the application)

### Project structure

The project follows a Spring MVC architecture. I chose not to implement Hexagonal Architecture, due to the small scope of the application, but in a production scenario, I would consider it for better separation of concerns and testability.

### API Documentation
I've included a spring boot starter for Swagger which automatically generates interactive API documentation. This can be accessed at `http://localhost:8080/swagger-ui.html` once the application is running.

### Security

The application is secured using Spring Security with Basic Auth for local development purposes. Default credentials are provided in the README, but these should not be used for production environments.
In a production environment, I would definitely implement OAuth2 or JWT based authentication for better security as is standard practice. I chose not to in the POC, because it requires setting up additional services such as Keycloak or another JWT issuer which would have added significant complexity to the project.

This project uses many of Spring Security's default configurations to keep things simple. In production,
I would use a key vault or secret management system to securely store sensitive information such as credentials or other sensitive data.

### Database Migrations

Flyway is used for managing database migrations. This ensures that the database schema is versioned and can be easily updated as the application evolves.
The scope of this application is small, so I did not create multiple migration files, but in a real-world scenario, each change to the database schema would be captured in its own migration file.

### Dockerization

I used docker-compose to simplify running the application + the database locally. 

### Actuator endpoints

Spring Boot Actuator is added to expose `/health` endpoints. This is crucial for monitoring the application's health in a production environment and enables compatibility with services such as Kubernetes through `/liveness` and `/readiness` endpoints.