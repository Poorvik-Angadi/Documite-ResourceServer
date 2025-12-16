# Documite-ResourceServer

A REST API Resource Server built with Spring Boot that provides CRUD operations for managing resources and users with authentication and authorization.

## Features

- **Resource Management**: Create, read, update, and delete resources
- **User Management**: Manage users with role-based access control
- **Authentication**: Secured endpoints with Spring Security
- **Authorization**: Role-based access (USER and ADMIN roles)
- **Database**: H2 in-memory database for quick setup
- **Comprehensive Testing**: 69 test cases covering unit, integration, and security scenarios

## Technology Stack

- **Java**: 17
- **Spring Boot**: 3.1.5
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Database operations
- **H2 Database**: In-memory database
- **Maven**: Build and dependency management
- **JUnit 5**: Testing framework

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Build the Project

```bash
mvn clean install
```

### Run Tests

```bash
mvn test
```

### Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Resource Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/resources` | Create a new resource | Yes (USER/ADMIN) |
| GET | `/api/resources` | Get all resources | Yes (USER/ADMIN) |
| GET | `/api/resources/{id}` | Get resource by ID | Yes (USER/ADMIN) |
| GET | `/api/resources?ownerId={id}` | Filter by owner | Yes (USER/ADMIN) |
| GET | `/api/resources?type={type}` | Filter by type | Yes (USER/ADMIN) |
| GET | `/api/resources?search={name}` | Search by name | Yes (USER/ADMIN) |
| PUT | `/api/resources/{id}` | Update a resource | Yes (USER/ADMIN) |
| DELETE | `/api/resources/{id}` | Delete a resource | Yes (USER/ADMIN) |

### User Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/users` | Create a new user | Yes (USER/ADMIN) |
| GET | `/api/users` | Get all users | Yes (USER/ADMIN) |
| GET | `/api/users/{id}` | Get user by ID | Yes (USER/ADMIN) |
| PUT | `/api/users/{id}` | Update a user | Yes (USER/ADMIN) |
| DELETE | `/api/users/{id}` | Delete a user | Yes (USER/ADMIN) |

## Example Requests

### Create a Resource

```bash
curl -X POST http://localhost:8080/api/resources \
  -H "Content-Type: application/json" \
  -u user:password \
  -d '{
    "name": "Project Documentation",
    "description": "Documentation for the project",
    "type": "DOCUMENT",
    "ownerId": 1
  }'
```

### Get All Resources

```bash
curl -X GET http://localhost:8080/api/resources \
  -u user:password
```

### Create a User

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -u admin:password \
  -d '{
    "username": "john.doe",
    "password": "securePassword123",
    "email": "john.doe@example.com",
    "role": "USER"
  }'
```

## Testing

The project includes comprehensive test coverage:

- **Controller Tests**: 30 tests for REST endpoints
- **Service Tests**: 23 tests for business logic
- **Integration Tests**: 16 tests for full-stack scenarios

See [TEST_DOCUMENTATION.md](TEST_DOCUMENTATION.md) for detailed test documentation.

### Run All Tests

```bash
mvn test
```

### Run Specific Test Suite

```bash
# Run only controller tests
mvn test -Dtest=*ControllerTest

# Run only service tests
mvn test -Dtest=*ServiceTest

# Run only integration tests
mvn test -Dtest=*IntegrationTest
```

## Project Structure

```
src/
├── main/
│   ├── java/com/documite/resourceserver/
│   │   ├── config/              # Configuration classes
│   │   ├── controller/          # REST controllers
│   │   ├── model/               # Entity models
│   │   ├── repository/          # JPA repositories
│   │   ├── service/             # Business logic
│   │   └── ResourceServerApplication.java
│   └── resources/
│       └── application.properties
└── test/
    ├── java/com/documite/resourceserver/
    │   ├── controller/          # Controller unit tests
    │   ├── service/             # Service unit tests
    │   └── integration/         # Integration tests
    └── resources/
        └── application-test.properties
```

## Security

**⚠️ IMPORTANT: This is a sample/testing project. Do not use in production without proper security hardening.**

Current security configuration:
- All endpoints require authentication
- Basic HTTP authentication is configured
- Role-based access control (RBAC) for USER and ADMIN roles
- Password encoding with BCrypt (configured but not enforced in models)
- CSRF protection is **disabled** for stateless REST API testing

### For Production Use:
- Enable CSRF protection for web applications
- Implement proper password hashing before storing in database
- Use HTTPS/TLS for all communications
- Implement rate limiting and request throttling
- Add custom exception handling with proper error messages
- Use JWT or OAuth2 for token-based authentication
- Implement comprehensive input validation
- Add logging and monitoring for security events

## Configuration

### Database Configuration

Located in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=create-drop
```

### Test Configuration

Located in `src/test/resources/application-test.properties`:

```properties
spring.security.user.name=testuser
spring.security.user.password=testpass
spring.security.user.roles=USER,ADMIN
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes with tests
4. Run all tests to ensure they pass
5. Submit a pull request

## License

See [LICENSE](LICENSE) file for details.