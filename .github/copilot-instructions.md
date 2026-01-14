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

### Tzatziki Framework (by Decathlon)

This project uses **[Tzatziki](https://github.com/Decathlon/tzatziki)**, a BDD testing framework created by Decathlon that extends Cucumber with powerful built-in steps for HTTP testing, JPA entity management, and YAML/JSON data formatting.

> Tzatziki provides ready-to-use Cucumber steps making it easy to TDD Java microservices by focusing on an outside-in testing strategy.

**Tzatziki modules used:**
- `tzatziki-common` - Core utilities and patterns
- `tzatziki-core` - Object creation, assertion, templating and time management
- `tzatziki-http` - HTTP client steps (wraps RestAssured) for REST API testing
- `tzatziki-spring` - Spring Boot integration
- `tzatziki-spring-jpa` - JPA entity management steps with Testcontainers

**Configuration:**
- Glue packages: `com.decathlon.tzatziki.steps` (built-in steps) + `fr.robinjesson.mybudgetapi.steps` (custom steps)
- Feature files: `src/test/resources/features/`
- Uses Testcontainers with PostgreSQL for isolated test database

---

### Key Tzatziki Steps

#### HTTP Steps (from `tzatziki-http`)

```gherkin
# Anonymous requests
When we get "/endpoint"
When we post "/endpoint":
"""yml
field: value
"""
When we put "/endpoint":
When we delete "/endpoint"

# Authenticated requests (user defined with custom step)
When {user} get "/endpoint"
When {user} post "/endpoint":
When {user} put "/endpoint":
When {user} delete "/endpoint"

# Response status assertions (can use name, code, or both: OK, 200, OK_200)
Then we receive a status OK_200
Then we receive a status CREATED_201
Then we receive a status NO_CONTENT_204
Then we receive a status BAD_REQUEST_400
Then we receive a status FORBIDDEN_403
Then we receive a status NOT_FOUND_404

# Response body assertions
And we receive:
"""yml
field: expectedValue
"""

# Status and body together
Then we receive a status OK_200 and:
"""yml
message: Hello user!
"""

# Partial match (only specified fields, ignores extra fields)
And we receive only:
"""yml
- name: item1
- name: item2
"""

# Exact match with order
And we receive exactly:
"""yml
- name: item1
- name: item2
"""
```

#### JPA Entity Steps (from `tzatziki-spring-jpa`)

```gherkin
# Setup data BEFORE the scenario runs (uses "will contain")
Given that the {EntityName} entities will contain:
"""yml
- field1: value1
  field2: value2
  relation.id: foreignKeyValue
"""

# Setup with table format (alternative)
Given that the users table will contain:
   | firstName | lastName |
   | Darth     | Vader    |

# Clear table and insert only these rows
Given that the {EntityName} entities will contain only:
"""yml
- field: value
"""

# Assert data AFTER an action (uses "contain" without "will")
Then the {EntityName} entities contain:
"""yml
field: expectedValue
"""

# Assert table is empty
Then the users table contains nothing

# Assert with relationships (dot notation for foreign keys)
And the AccountEntity entities contain:
"""yml
- name: new account
  user.uid: robinj
"""
```

**Important:** With Hibernate 6.6+ / Spring Boot 3.4+, you cannot manually specify auto-generated IDs. Let the database generate them. For parent-child relationships, IDs follow predictable sequences starting at 1 (reset per scenario).

#### Assertion Flags (from `tzatziki-core`)

Tzatziki provides powerful assertion flags for flexible matching:

```gherkin
# Null checks
field: ?isNull
field: ?notNull

# Equality and negation
field: ?eq expectedValue
field: ?== expectedValue
field: ?not expectedValue
field: ?!= expectedValue

# Comparison operators
field: ?gt 10          # greater than
field: ?> 10
field: ?ge 10          # greater or equal
field: ?>= 10
field: ?lt 100         # less than
field: ?< 100
field: ?le 100         # less or equal
field: ?<= 100

# String matching
field: ?e ^pattern.*$  # regex matching
field: ?contains text
field: ?doesNotContain text

# Collection/Set membership
field: ?in ["a", "b", "c"]
field: ?notIn ["x", "y"]

# Type checks
field: ?is Boolean
field: ?is Number
field: ?isUUID

# Date/Time assertions
field: ?before {{@now}}
field: ?after {{@now}}

# Base64
field: ?base64 originalValue
```

#### Object Comparison Methods

```gherkin
# Contains at least the expected elements
Then response contains:

# Contains at least, in order
Then response contains in order:

# Contains only these elements (no extras)
Then response contains only:

# Contains only, in order
Then response contains only and in order:

# Exact match (same values)
Then response is equal to:

# Exactly these elements (literally)
Then response contains exactly:
```

---

### Templating with Handlebars

Tzatziki uses [Handlebars](https://github.com/jknack/handlebars.java) for templating. Variables from the context can be injected:

```gherkin
Given that userId is "123"
When we get "/users/{{userId}}"

# Assign variables while templating
Given that user is a User:
"""yml
id: 1
name: {{{[userName: bob]}}}
"""
Then userName is equal to "bob"
```

---

### Custom Steps (project-specific)

Located in `src/test/java/fr/robinjesson/mybudgetapi/steps/StepDefinitions.java`:

```gherkin
# Create an authenticated user context (adds JWT cookie to requests)
Given a user named {username}
Given a user named {username} with password {password}
```

---

### Test File Structure

```gherkin
Feature: [Feature Name]

  Background:
    # Setup authenticated user
    Given a user named robinj
    # Setup test data in database
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

  Scenario: [Happy path description]
    When robinj get "/endpoint"
    Then we receive a status OK_200
    And we receive:
    """yml
    expectedField: expectedValue
    """

  Scenario: [Authorization test - user can only access own resources]
    Given a user named otherUser
    When otherUser get "/endpoint/{id}"
    Then we receive a status FORBIDDEN_403

  Scenario: [Resource not found]
    When robinj get "/endpoint/unknown-id"
    Then we receive a status NOT_FOUND_404
```

---

### Test Class Configuration

**CucumberTest.java** - Main test runner:
```java
@RunWith(Cucumber.class)
@CucumberOptions(
    plugin = "pretty",
    tags = "not @ignore",
    features = "classpath:features",
    glue = {
        "fr.robinjesson.mybudgetapi.steps",
        "com.decathlon.tzatziki.steps"
    })
public class CucumberTest {}
```

**AzertyApplicationSteps.java** - Spring Boot context with Testcontainers:
```java
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = MyBudgetApplication.class)
@ContextConfiguration(initializers = AzertyApplicationSteps.Initializer.class)
public class AzertyApplicationSteps {
    // PostgreSQL Testcontainer initialization
}
```

---

### Test Scenarios to Cover

1. **Happy path** - Normal successful operation
2. **Authorization** - User can only access their own resources
3. **Error cases** - 404 (not found), 403 (forbidden), 400 (bad request)
4. **Edge cases** - Empty lists, null values, validation errors

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

