# Spring Boot Recipes

### Requirements

- Java >= 25.0.1
- Docker

### Running the Application
To run the application, use the following command:

```bash
docker-compose up --build
```

This command builds and starts the application along with its dependencies (like Postgres) using Docker Compose.

### Documentation

This project uses Swagger for API documentation. Once the application is running, you can access the Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

### Authentication

The application is secured using Basic Authentication for local development purposes. Use the following default credentials to access the API:
- Username: `user`
- Password: `password`