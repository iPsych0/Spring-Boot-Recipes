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

### Example API Requests

The application is hosted on `http://localhost:8080`.

The application exposes the following endpoints for managing recipes:
- `POST /api/v1/recipes` - Add a new recipe
- `PUT /api/v1/recipes/{id}` - Update an existing recipe
- `DELETE /api/v1/recipes/{id}` - Remove a recipe
- `GET /api/v1/recipes/{id}` - Fetch a recipe by ID
- `GET /api/v1/recipes` - Fetch all recipes with optional filtering

Example payload:
```json
{
  "name": "Pasta bolognese",
  "vegetarian": false,
  "servings": 2,
  "ingredients": "Pasta, beef, tomato sauce, cheese",
  "instructions": "Boil pasta. Cook meat with sauce. Combine and serve."
}
```

Example filter request:
```
GET /api/v1/recipes?vegetarian=false&servings=2&include=pasta&text=boil
```

Curl example for creating a Recipe:
```bash
curl -u user:password -X POST http://localhost:8080/api/v1/recipes \
-H "Content-Type: application/json" \
-d '{
  "name": "Pasta bolognese",
  "vegetarian": false,
  "servings": 2,
  "ingredients": "Pasta, beef, tomato sauce, cheese",
  "instructions": "Boil pasta. Cook meat with sauce. Combine and serve."
}'
```
Curl example for filtering Recipes:
```bash
curl -u user:password -X GET "http://localhost:8080/api/v1/recipes?vegetarian=false&servings=2&include=pasta&text=boil"
```

### Health Check
The application exposes health check endpoints via Spring Boot Actuator. You can check the health of the application at:

```
http://localhost:8080/actuator/health
```
