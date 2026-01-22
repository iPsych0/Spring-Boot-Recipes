# Spring Boot Recipes

### Requirements

- Java >= 25.0.1
- Gradle >= 9.2.1
- Docker/Podman
- docker-compose
- Postman/Insomnia (optional, for API testing)

### Running the Application
For convenience, a Makefile is included in this project. You can start the application using the following command:

```bash
make run
```

(Or manually: `docker-compose up --build`)

> If you are using Podman, please use `make run-podman` instead.

### Documentation

This project uses Swagger for API documentation. Once the application is running, you can access the Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

Architectural decisions can be found in [ARCHITECTURE.md](ARCHITECTURE.md).

### Authentication

The application is secured using Basic Authentication for local development purposes. Use the following default credentials to access the API:
- Username: `user`
- Password: `password`

### Running Tests

> Running the tests requires Java 25 to be installed on your local machine.
Consider using `sdkman` to install Java 25 easily: https://sdkman.io/install.

To run the unit tests, use the following command:

```bash
make unit-test
```

(Or manually: `./gradlew test`)

To run the integration tests, use the following command:

```bash
make integration-test
```
(Or manually: `./gradlew integrationTest`)