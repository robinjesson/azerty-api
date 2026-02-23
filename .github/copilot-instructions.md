# GitHub Copilot Instructions - MyBudget API

## Project Overview

**MyBudget API** is a personal budget management REST API built with **Spring Boot 3.5.9**, **Java 25**, and **PostgreSQL**. It projects future financial balances using asynchronous transaction generation and event-driven Kafka messaging.

### Tech Stack

- **Framework**: Spring Boot 3.5.9
- **Language**: Java 25
- **Database**: PostgreSQL (Spring Data JPA)
- **Security**: Spring Security + JWT (HttpOnly cookies)
- **Mapping**: MapStruct 1.6.3
- **Testing**: Tzatziki (BDD via Cucumber) with Testcontainers + JaCoCo coverage
- **Code Quality**: SonarCloud
- **Utilities**: Lombok, SpringDoc OpenAPI

---

## Build, Test & Commands

### Build & Run

```bash
# Clean build with all tests
./mvnw clean install

# Run application locally (uses Testcontainers PostgreSQL in tests)
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Run only integration tests (Tzatziki)
./mvnw test

# Generate JaCoCo coverage report
./mvnw jacoco:report
# Coverage report: target/site/jacoco/index.html

# Compile only (skip tests)
./mvnw clean compile
```

### Local Development

```bash
# Start infrastructure (PostgreSQL)
podman-compose up -d

# Stop infrastructure
podman-compose down
```

Environment variables needed (create `.env` file):
```
DB_NAME=mybudget
DB_USER=user
DB_PASSWORD=password
JWT_SECRET_KEY=your-secret-key-here
```

---

## Architecture

This project follows **strict layered architecture** with clear separation of concerns:

```
api/           → REST Controllers (entry points, HTTP requests/responses)
adapter/       → Adapters (orchestrate between API and Business layers)
businesses/    → Business logic, services, validation
entities/      → JPA Entities with audit fields
repository/    → Spring Data JPA Repositories
mappers/       → MapStruct interfaces for DTO ↔ Entity mapping
security/      → JWT authentication, SecurityConfig, ConnectedUser
exception/     → Custom exceptions, global exception handler
```

### Data Flow

1. **Controller** receives request → delegates to **Adapter**
2. **Adapter** maps Request DTO → calls **Business** logic
3. **Business** validates & performs operations → calls **Repository**
4. **Repository** persists to database → returns **Entity**
5. **Adapter** maps Entity → Response DTO → **Controller** returns it

---

## Naming Conventions

Always follow these patterns—they are fundamental to code discovery:

| Component | Pattern | Example |
|-----------|---------|---------|
| Controllers | `*Controller.java` | `AccountController` |
| Adapters | `*Adapter.java` | `AccountAdapter` |
| Business Services | `*Business.java` | `AccountBusiness` |
| Entities | `*Entity.java` | `AccountEntity` |
| Repositories | `*Repository.java` extends `JpaRepository` | `AccountRepository` |
| Mappers | `*Mapper.java` | `AccountMapper` |
| Request DTOs | `*Request.java` | `AccountCreationRequest` |
| Response DTOs | `*Response.java` | `AccountResponse` |

---

## Code Style & Key Patterns

### Controllers
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

**Important:**
- Always use `@RequiredArgsConstructor` with `final` fields (no `@Autowired`)
- Return appropriate HTTP status codes (200, 201, 403, 404, etc.)
- Use `ResponseEntity<T>` for all responses

### Adapters
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

**Important:** Adapters orchestrate mapping and delegate to business layer

### Business Layer
```java
@Service
@RequiredArgsConstructor
public class ResourceBusiness {
    private final ResourceRepository resourceRepository;
    private final ConnectedUser connectedUser;
    
    public List<ResourceEntity> findAll() {
        // ALWAYS scope access to connected user
        return resourceRepository.findByUserUid(connectedUser.getUid());
    }
}
```

**Critical:** Always scope data access to `connectedUser.getUid()` for authorization

### Exception Handling

Use custom exceptions for all error cases:
```java
throw new ForbiddenException("User does not own this resource");  // 403
throw new NotFoundException("Account not found");                   // 404
throw new BadRequestException("Invalid input data");               // 400
```

### Entities

All entities must:
- Extend or embed `Timestamp` for audit fields (`createdAt`, `updatedAt`)
- Use `@GeneratedValue(strategy = GenerationType.IDENTITY)` for IDs
- Use `Long` type for identifiers
- Use Lombok annotations: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`

### MapStruct Mappers

```java
@Mapper(config = MapperConfiguration.class)
public interface ResourceMapper {
    // Create new entity from request (POST)
    ResourceEntity mapToEntity(ResourceRequest request);
    
    // Update existing entity from request (PUT/PATCH)
    void mapToExistingEntity(@MappingTarget ResourceEntity target, ResourceRequest source);
    
    // Map entity to response
    ResourceResponse mapToResponse(ResourceEntity entity);
    List<ResourceResponse> mapToResponse(List<ResourceEntity> entities);
}
```

**Important:** 
- Mappers must be interfaces, not classes
- All must use `config = MapperConfiguration.class`
- Use `@MappingTarget` for update operations (PUT/PATCH)—maps source fields onto existing target entity
- `mapToEntity()` is for CREATE operations
- `mapToExistingEntity()` is for UPDATE operations

---

## Testing with Tzatziki (BDD Integration Tests)

**Tzatziki** is a Decathlon BDD framework extending Cucumber with built-in HTTP & JPA steps. Tests run against a real PostgreSQL instance via Testcontainers.

### Feature Files Location
`src/test/resources/features/`

### Test Structure Example

```gherkin
Feature: Account Management

  Background:
    # Create authenticated user context
    Given a user named robinj
    # Populate test database
    And that the UserEntity entities will contain:
    """yml
    - uid: robinj
      email: robinj@email.fr
      password: x
    """
    And that the AccountEntity entities will contain:
    """yml
    - name: compte 1
      user.uid: robinj
      startAmount: 10
    """

  Scenario: Retrieve account
    When robinj get "/accounts"
    Then we receive a status OK_200
    And we receive:
    """yml
    name: compte 1
    """

  Scenario: Create account - verify DB state
    When robinj post "/accounts":
    """yml
    name: new account
    startAmount: 500
    """
    Then we receive a status CREATED_201
    And we receive:
    """yml
    name: new account
    """
    # ALWAYS verify DB state after POST/PUT
    And that the AccountEntity entities contain:
    """yml
    - name: new account
      startAmount: 500
      user.uid: robinj
    """

  Scenario: Authorization - user cannot access other's account
    Given a user named otherUser
    When otherUser get "/accounts/1"
    Then we receive a status FORBIDDEN_403

  Scenario: Not found
    When robinj get "/accounts/999"
    Then we receive a status NOT_FOUND_404
```

### Tzatziki Rules (Critical)

1. **Auto-generated IDs**: Hibernate assigns IDs sequentially (1, 2, 3...) in order of creation in a scenario
   - First entity created = `id: 1`
   - Use numeric IDs in requests: `GET /accounts/1`

2. **Always verify database state after POST/PUT mutations**
   - Add `And that the [Entity]Entity entities contain:` after successful mutations
   - Verify key business fields were correctly saved

3. **Avoid LazyInitializationException**
   - Don't verify `@ManyToMany` or `@OneToMany` collections in DB assertions
   - Verify collections in HTTP response instead (they're properly loaded)
   - Only verify direct fields and foreign key IDs in DB assertions

4. **Custom Authenticated Step** (defined in `src/test/java/fr/robinjesson/mybudgetapi/steps/`)
   ```gherkin
   Given a user named {username}
   Given a user named {username} with password {password}
   ```
   - Automatically adds JWT cookie to all subsequent requests as that user

### Test Configuration

- **CucumberTest.java**: Main test runner with glue packages configured
- **AzertyApplicationSteps.java**: Spring Boot context initialization with Testcontainers PostgreSQL
- Testcontainers automatically handles test database lifecycle
- Tests run in parallel; each gets isolated PostgreSQL instance

---

## Security

### Authentication & Authorization

- **JWT stored in HTTP-only cookies** (no XSS vulnerability)
- **ConnectedUser** component injected in Business layer provides current user info
- Always verify resource ownership: `connectedUser.getUid()`

### Scoping Data Access

```java
// ✅ CORRECT - scope to current user
return repository.findByUserUid(connectedUser.getUid());

// ❌ WRONG - returns all users' data
return repository.findAll();
```

---

## Creating New Features (Step-by-Step)

1. **Create Entity** in `entities/` with JPA annotations
   - Add Lombok annotations
   - Include `Timestamp` for audit fields
2. **Create Repository** in `repository/` extending `JpaRepository`
   - Add custom finder methods (e.g., `findByUserUid()`)
3. **Create Request/Response DTOs** in `api/request/` and `api/response/`
4. **Create Mapper** in `mappers/` (interface with `@Mapper` annotation)
5. **Create Business** in `businesses/` with validation & logic
   - Inject `ConnectedUser` for authorization
   - Throw appropriate exceptions
6. **Create Adapter** in `adapter/` for orchestration
7. **Create Controller** in `api/` with REST endpoints
8. **Write Integration Tests** as `.feature` files
   - Happy path with DB verification
   - Authorization tests (403)
   - Error cases (404, 400)

---

## Important Notes

- **User Scoping**: Always check `connectedUser.getUid()` before returning data
- **Error Handling**: Use custom exceptions; global `@ControllerAdvice` handles mapping to HTTP statuses
- **French in Tests**: French comments are acceptable in Gherkin feature files
- **No Passwords in Code**: All secrets via environment variables (checked by SonarCloud)
- **Zero Trust Security**: Always assume requests could be malicious; verify ownership

---

## IDE / Development Tips

- IntelliJ IDEA recognizes Cucumber glue packages automatically
- MapStruct annotation processor runs during compilation (configured in `pom.xml`)
- Lombok annotation processor also configured (order matters in `pom.xml`)
- Swagger UI available at `http://localhost:8080/swagger-ui.html` when running
- JaCoCo reports reveal untested code paths; aim for >80% coverage
