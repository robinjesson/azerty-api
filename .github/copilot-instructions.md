# GitHub Copilot Instructions - MyBudget API

## Project Overview

**MyBudget API** is a personal budget management REST API built with **Spring Boot 4.0.3**, **Java 25**, and **PostgreSQL**. It projects future financial balances using asynchronous transaction generation and event-driven Kafka messaging.

### Tech Stack

- **Framework**: Spring Boot 4.0.3
- **Language**: Java 25
- **Database**: PostgreSQL (Spring Data JPA)
- **Security**: Spring Security + JWT (HttpOnly cookies)
- **Mapping**: MapStruct 1.6.3
- **HATEOAS**: Spring HATEOAS (Richardson Level 3)
- **API Docs**: SpringDoc OpenAPI 3.0.2 (Swagger UI)
- **Testing**: Tzatziki (BDD via Cucumber) with Testcontainers + JaCoCo coverage
- **Code Quality**: SonarCloud
- **Utilities**: Lombok

---

## Build, Test & Commands

### Build & Run

```bash
# Clean build with all tests (requires JAVA_HOME pointing to Java 25)
JAVA_HOME=/usr/lib/jvm/temurin-25-jdk-amd64 ./mvnw clean install

# Run application locally
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
api/
├── assembler/   → HATEOAS RepresentationModelAssemblers (EntityModel builders)
├── config/      → OpenAPI configuration
├── request/     → Request DTOs
├── response/    → Response DTOs
└── *Controller  → REST Controllers (entry points)
adapter/         → Adapters (orchestrate between API and Business layers)
businesses/      → Business logic, services, validation
entities/        → JPA Entities with audit fields
repository/      → Spring Data JPA Repositories
mappers/         → MapStruct interfaces for DTO ↔ Entity mapping
security/        → JWT authentication, SecurityConfig, ConnectedUser
exception/       → Custom exceptions, global exception handler
```

### Data Flow

1. **Controller** receives request → delegates to **Adapter**
2. **Adapter** maps Request DTO → calls **Business** logic
3. **Business** validates & performs operations → calls **Repository**
4. **Repository** persists to database → returns **Entity**
5. **Adapter** maps Entity → Response DTO → **Controller** wraps it in `EntityModel` via **Assembler**

---

## API Design — Richardson Maturity Model Level 3

The API follows **Richardson Maturity Model Level 3 (HATEOAS)**:

- **Level 1 ✅ Resources**: Distinct URIs per resource (`/v0/accounts`, `/v0/tags`, ...)
- **Level 2 ✅ HTTP Verbs**: `GET` (read), `POST` (create), `PUT` (full update), `DELETE` (delete)
- **Level 3 ✅ HATEOAS**: Every response includes `_links` with navigable hypermedia controls

### Response format (single resource)

```json
{
  "id": 1,
  "name": "compte courant",
  "startAmount": 1000.00,
  "_links": {
    "self":     { "href": "http://host/v0/accounts/1" },
    "accounts": { "href": "http://host/v0/accounts"   }
  }
}
```

### Response format (collection)

Collections are returned as **JSON arrays** where each item has its own `_links`. This preserves compatibility with Tzatziki BDD assertions:

```json
[
  { "id": 1, "name": "compte 1", "_links": { "self": { "href": "..." } } },
  { "id": 2, "name": "compte 2", "_links": { "self": { "href": "..." } } }
]
```

---

## API Versioning Strategy

### Current version: **v0**

All endpoints are prefixed with `/v{MAJOR}/`. The current version is **v0**, aligned with the Maven project version `0.0.1-SNAPSHOT`.

### URL structure

```
/v0/accounts
/v0/accounts/{id}
/v0/accounts/{accountId}/transactions   ← nested resource (transactions scoped to an account)
/v0/transactions                        ← cross-account transactions for the connected user
/v0/transactions/{id}
/v0/tags
/v0/auth/signup
/v0/auth/login
/v0/users/me
```

### When to create a new version

Create a new version **only** for **breaking changes**:

| Breaking (new version needed) | Non-breaking (no new version) |
|---|---|
| Removing a response field | Adding an optional request field |
| Changing a field type | Adding a new response field |
| Changing endpoint semantics | Adding a new endpoint |
| Renaming an endpoint | Bug fixes |

### How to implement v1 alongside v0

See **[API-VERSIONING.md](../API-VERSIONING.md)** for the complete step-by-step guide, including:
- Controller structure (`api/v1/`)
- DTO versioning (`api/response/v1/`)
- HATEOAS assembler per version
- Deprecation headers (`Deprecation`, `Sunset`, `Link`)
- Swagger group separation

---

## Naming Conventions

| Component | Pattern | Example |
|-----------|---------|---------|
| Controllers (current version) | `*Controller.java` | `AccountController` |
| Controllers (future versions) | `v{N}/*V{N}Controller.java` | `v1/AccountV1Controller` |
| Assemblers | `*ModelAssembler.java` | `AccountModelAssembler` |
| Adapters | `*Adapter.java` | `AccountAdapter` |
| Business Services | `*Business.java` | `AccountBusiness` |
| Entities | `*Entity.java` | `AccountEntity` |
| Repositories | `*Repository.java` extends `JpaRepository` | `AccountRepository` |
| Mappers | `*Mapper.java` | `AccountMapper` |
| Request DTOs | `*Request.java` | `AccountCreationRequest` |
| Response DTOs (current) | `*Response.java` | `AccountResponse` |
| Response DTOs (future) | `v{N}/*V{N}Response.java` | `v1/AccountV1Response` |

---

## Code Style & Key Patterns

### Controllers

Controllers **never** build response objects directly. They delegate to Adapters and then wrap results using Assemblers:

```java
@RestController
@RequestMapping("/v0/resource")
@RequiredArgsConstructor
@Tag(name = "Resources", description = "Manage resources for the connected user")
public class ResourceController {

    private final ResourceAdapter resourceAdapter;
    private final ResourceModelAssembler resourceModelAssembler;

    @GetMapping
    @Operation(summary = "Get all resources for the connected user")
    @ApiResponse(responseCode = "200", description = "List of resources")
    public ResponseEntity<List<EntityModel<ResourceResponse>>> findAll() {
        return ResponseEntity.ok(resourceAdapter.findAll().stream()
                .map(resourceModelAssembler::toModel)
                .toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one resource by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resource found"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    public ResponseEntity<EntityModel<ResourceResponse>> findById(@PathVariable final Long id) {
        return ResponseEntity.ok(resourceModelAssembler.toModel(resourceAdapter.findById(id)));
    }

    @PostMapping
    @Operation(summary = "Create a new resource")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Resource created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    public ResponseEntity<EntityModel<ResourceResponse>> create(
            @RequestBody @Valid final ResourceRequest request) {
        return new ResponseEntity<>(
                resourceModelAssembler.toModel(resourceAdapter.create(request)),
                HttpStatus.CREATED);
    }
}
```

**Important:**
- Always use `@RequiredArgsConstructor` with `final` fields (no `@Autowired`)
- Always annotate with `@Tag` (OpenAPI) and `@Operation` / `@ApiResponse` per method
- Always return `EntityModel<T>` for single resource, `List<EntityModel<T>>` for collections
- `POST` → `201 CREATED`, `GET` → `200 OK`, `PUT` → `200 OK`, `DELETE` → `204 NO_CONTENT`
- Signup endpoint returns `201 CREATED` (not 200)

### HATEOAS Assemblers

```java
@Component
public class ResourceModelAssembler
        implements RepresentationModelAssembler<ResourceResponse, EntityModel<ResourceResponse>> {

    @Override
    public EntityModel<ResourceResponse> toModel(final ResourceResponse response) {
        return EntityModel.of(response,
                linkTo(methodOn(ResourceController.class).findById(response.id())).withSelfRel(),
                linkTo(methodOn(ResourceController.class).findAll()).withRel("resources"));
    }
}
```

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

**Important:** Adapters orchestrate mapping and delegate to business layer. They return plain DTOs — assemblers are applied in the controller layer.

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

**Critical:** Always scope data access to `connectedUser.getUid()` for authorization.

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
    ResourceEntity mapToEntity(ResourceRequest request);
    void mapToExistingEntity(@MappingTarget ResourceEntity target, ResourceRequest source);
    ResourceResponse mapToResponse(ResourceEntity entity);
    List<ResourceResponse> mapToResponse(List<ResourceEntity> entities);
}
```

**Important:**
- Mappers are interfaces, not classes
- All use `config = MapperConfiguration.class`
- `mapToEntity()` → CREATE; `mapToExistingEntity()` → UPDATE

---

## Testing with Tzatziki (BDD Integration Tests)

**Tzatziki** is a Decathlon BDD framework extending Cucumber. Tests run against a real PostgreSQL instance via Testcontainers.

### Feature Files Location
`src/test/resources/features/`

### Test Structure Example

```gherkin
Feature: Account Management

  Background:
    Given a user named robinj
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

  Scenario: Retrieve accounts
    When robinj get "/v0/accounts"
    Then we receive a status OK_200
    And we receive:
    """yml
    - name: compte 1
    """

  Scenario: Create account - verify DB state
    When robinj post "/v0/accounts":
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
    When otherUser get "/v0/accounts/1"
    Then we receive a status FORBIDDEN_403

  Scenario: Not found
    When robinj get "/v0/accounts/999"
    Then we receive a status NOT_FOUND_404
```

### Tzatziki Rules (Critical)

1. **Always use `/v0/` prefix** in all endpoint URLs in feature files.

2. **Transactions are a sub-resource of accounts**:
   - `POST /v0/accounts/{accountId}/transactions` → create on an account
   - `GET /v0/accounts/{accountId}/transactions` → list for an account
   - `GET /v0/transactions` → list all user's transactions (cross-account)
   - `PUT /v0/transactions/{id}` → update a transaction

3. **Auto-generated IDs**: Hibernate assigns IDs sequentially (1, 2, 3...) in order of creation.
   - First entity created = `id: 1`. Use numeric IDs in requests: `GET /v0/accounts/1`

4. **Always verify database state after POST/PUT mutations**:
   - Add `And that the [Entity]Entity entities contain:` after successful mutations.

5. **HATEOAS `_links` in responses**: Tzatziki partial matching ignores `_links`. Tests checking specific fields (`name`, `amount`, etc.) continue to work without modification.

6. **Avoid LazyInitializationException**:
   - Don't verify `@ManyToMany` or `@OneToMany` collections in DB assertions.
   - Verify collections in HTTP response instead.

7. **Custom Authenticated Step**:
   ```gherkin
   Given a user named {username}
   Given a user named {username} with password {password}
   ```

---

## Security

### Authentication & Authorization

- **JWT stored in HTTP-only cookies** (no XSS vulnerability)
- **ConnectedUser** component injected in Business layer provides current user info
- Always verify resource ownership: `connectedUser.getUid()`
- Auth endpoints (`/v0/auth/**`) are whitelisted in `SecurityConfiguration`

### Scoping Data Access

```java
// ✅ CORRECT - scope to current user
return repository.findByUserUid(connectedUser.getUid());

// ❌ WRONG - returns all users' data
return repository.findAll();
```

---

## Creating New Features (Step-by-Step)

1. **Create Entity** in `entities/` with JPA annotations and Lombok
2. **Create Repository** in `repository/` extending `JpaRepository`
3. **Create Request/Response DTOs** in `api/request/` and `api/response/`
4. **Create Mapper** in `mappers/` (interface with `@Mapper` annotation)
5. **Create Business** in `businesses/` with validation & logic
6. **Create Adapter** in `adapter/` for orchestration
7. **Create ModelAssembler** in `api/assembler/` implementing `RepresentationModelAssembler`
8. **Create Controller** in `api/` with `/v0/` prefix, `@Tag`, `@Operation`, `@ApiResponse`
9. **Write Integration Tests** as `.feature` files using `/v0/` prefix

---

## Important Notes

- **User Scoping**: Always check `connectedUser.getUid()` before returning data
- **Error Handling**: Use custom exceptions; global `@ControllerAdvice` handles mapping to HTTP statuses
- **French in Tests**: French comments are acceptable in Gherkin feature files
- **No Passwords in Code**: All secrets via environment variables (checked by SonarCloud)
- **Zero Trust Security**: Always assume requests could be malicious; verify ownership
- **Versioning**: See `API-VERSIONING.md` for the complete versioning strategy

---

## IDE / Development Tips

- IntelliJ IDEA recognizes Cucumber glue packages automatically
- MapStruct annotation processor runs during compilation (configured in `pom.xml`)
- Lombok annotation processor also configured (order matters in `pom.xml`)
- Swagger UI available at `http://localhost:8080/swagger-ui.html` when running
- JaCoCo reports reveal untested code paths; aim for >80% coverage

