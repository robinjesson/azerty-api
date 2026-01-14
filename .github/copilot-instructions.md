# GitHub Copilot Instructions - MyBudget API

## Project Overview

**MyBudget API** is a personal budget management REST API built with **Spring Boot 3.5** and **Java 21**.

### Tech Stack

- **Framework**: Spring Boot 3.5.9
- **Language**: Java 21
- **Database**: PostgreSQL with Spring Data JPA
- **Security**: Spring Security with JWT (jjwt 0.13.0)
- **Mapping**: MapStruct 1.6.0
- **Utilities**: Lombok
- **API Documentation**: SpringDoc OpenAPI (Swagger UI)
- **Testing**: Cucumber (Tzatziki) for BDD integration tests, JaCoCo for coverage
- **Code Quality**: SonarCloud

---

## Architecture

This project follows a **layered architecture** with clear separation of concerns:

```
api/           → REST Controllers (entry points)
adapter/       → Adapters (orchestration between API and Business layers)
businesses/    → Business logic / Services
entities/      → JPA Entities
repository/    → Spring Data JPA Repositories
mappers/       → MapStruct mappers
security/      → Security configuration and JWT handling
exception/     → Custom exceptions and global exception handler
```

### Layer Responsibilities

1. **Controller** (`api/`): Handles HTTP requests/responses, delegates to Adapter
2. **Adapter** (`adapter/`): Orchestrates mapping and business calls, transforms DTOs ↔ Entities
3. **Business** (`businesses/`): Contains business logic, validation, and rules
4. **Repository** (`repository/`): Data access layer using Spring Data JPA
5. **Mapper** (`mappers/`): MapStruct interfaces for object mapping

---

## Coding Conventions

### Naming Conventions

- **Controllers**: `*Controller.java` (e.g., `AccountController`)
- **Adapters**: `*Adapter.java` (e.g., `AccountAdapter`)
- **Business Services**: `*Business.java` (e.g., `AccountBusiness`)
- **Entities**: `*Entity.java` (e.g., `AccountEntity`)
- **Repositories**: `*Repository.java` (e.g., `AccountRepository`)
- **Mappers**: `*Mapper.java` (e.g., `AccountMapper`)
- **Request DTOs**: `*Request.java` (e.g., `AccountCreationRequest`)
- **Response DTOs**: `*Response.java` (e.g., `AccountResponse`)

### Code Style

- Use **Lombok** annotations: `@RequiredArgsConstructor`, `@Getter`, `@Setter`
- Use **constructor injection** via `@RequiredArgsConstructor` (no `@Autowired`)
- Mark method parameters as `final` when appropriate
- Use `ResponseEntity<T>` for controller responses
- Return appropriate HTTP status codes (200, 201, 403, 404, etc.)

### Entity Guidelines

- All entities extend or embed `Timestamp` for audit fields
- Use `@GeneratedValue(strategy = GenerationType.UUID)` for primary keys
- Use `UUID` type for identifiers

### MapStruct Configuration

- All mappers use `@Mapper(config = MapperConfiguration.class)`
- Mappers are interfaces, not classes

---

## Security

- JWT-based authentication stored in HTTP-only cookies
- `ConnectedUser` component provides access to the current authenticated user
- Always verify resource ownership before returning data (check `connectedUser.getUid()`)

---

## Testing

### Integration Tests (Cucumber/Tzatziki)

- Feature files located in `src/test/resources/features/`
- Use YAML format for test data in Gherkin steps
- Test scenarios should cover:
  - Happy path
  - Authorization (user can only access their own resources)
  - Error cases (404, 403, 400)

### Test File Structure

```gherkin
Feature: [Feature Name]

  Background:
    Given a user named [username]
    And that the [Entity] entities will contain:
    """yml
    - field: value
    """

  Scenario: [Scenario description]
    When [user] [method] "[endpoint]"
    Then we receive a status [STATUS_CODE]
    And we receive:
    """yml
    expected: response
    """
```

---

## When Generating Code

### Creating a new feature

1. Create the **Entity** in `entities/` with JPA annotations and Lombok
2. Create the **Repository** interface in `repository/` extending `JpaRepository`
3. Create **Request/Response DTOs** in `api/request/` and `api/response/`
4. Create the **Mapper** interface in `mappers/`
5. Create the **Business** service in `businesses/` with business logic
6. Create the **Adapter** in `adapter/` for orchestration
7. Create the **Controller** in `api/` with REST endpoints
8. Add **integration tests** as `.feature` files

### Example patterns to follow

**Controller**:
```java
@RestController
@RequestMapping("/resource")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceAdapter resourceAdapter;
    
    @GetMapping
    public ResponseEntity<List<ResourceResponse>> findAll() {
        return ResponseEntity.ok(resourceAdapter.findAll());
    }
}
```

**Adapter**:
```java
@Service
@RequiredArgsConstructor
public class ResourceAdapter {
    private final ResourceBusiness resourceBusiness;
    private final ResourceMapper resourceMapper;
    
    public List<ResourceResponse> findAll() {
        return resourceMapper.mapToResponse(resourceBusiness.findAll());
    }
}
```

**Business**:
```java
@Service
@RequiredArgsConstructor
public class ResourceBusiness {
    private final ResourceRepository resourceRepository;
    private final ConnectedUser connectedUser;
    
    public List<ResourceEntity> findAll() {
        return resourceRepository.findByUserUid(connectedUser.getUid());
    }
}
```

---

## Commands

- **Build**: `./mvnw clean install`
- **Run tests**: `./mvnw test`
- **Run locally**: `./mvnw spring-boot:run -Dspring-boot.run.profiles=local`
- **Generate coverage report**: `./mvnw jacoco:report`

---

## Important Notes

- Always scope data access to the connected user (`connectedUser.getUid()`)
- Throw `ForbiddenException` for unauthorized access attempts
- Throw `NotFoundException` when resources don't exist
- Use `BadRequestException` for validation errors
- French comments are acceptable in test files

