# Arquitectura del Sistema

## Estructura de Agentes (Roles Virtuales)
1. **@ARCHITECT:** Diseña contratos OpenAPI (`.yaml`) y Records Java (DTOs). No toca lógica.
2. **@CORE_DEV:** Implementa Dominio puro (DDD). Bounded Contexts y Casos de Uso.
3. **@INFRA_DEV:** "The Plumber". Conecta Spring Boot, JPA, Docker y Flyway.

---

## Package Structure (Hexagonal Architecture)

```
com.othertales.modules.<module>/
├── domain/                    # Pure Java - NO Spring annotations
│   ├── <Entity>.java         # Domain entities (records or POJOs)
│   ├── <ValueObject>.java    # Immutable value objects
│   └── <Exception>.java      # Domain-specific exceptions
│
├── application/
│   ├── port/                 # Interfaces (Ports)
│   │   └── <Repository>.java
│   ├── usecase/              # Application Services
│   │   ├── <Action>UseCase.java    # Single-action orchestrations
│   │   └── <Entity>Service.java    # CRUD aggregators (see ADR-005)
│   └── dto/                  # Data Transfer Objects (records)
│
└── infrastructure/
    ├── persistence/          # JPA Adapters
    ├── web/                  # REST Controllers
    └── config/               # Spring @Configuration
```

---

## Decisiones Arquitectónicas (ADR)

### ADR-001: API First
Definimos el contrato OpenAPI antes de escribir el Controller.

### ADR-002: DB Schema
Flyway es la única fuente de verdad para la estructura de la BD.

### ADR-003: Database
PostgreSQL hosted on Supabase.

### ADR-004: Authentication
OAuth2 Resource Server - JWT validation against Supabase Auth.
- Authentication is handled entirely by Supabase (`auth.users`).
- Backend validates JWTs using Supabase's JWKS endpoint.
- User profiles stored in `public.profiles` table.

### ADR-005: UseCase vs Service Pattern
**Context:** Inconsistency between `CreateProjectUseCase` (single action) and `ChapterService` (CRUD aggregator).

**Decision:**
- **UseCase classes** (`<Action>UseCase.java`): Reserved for complex business orchestrations involving multiple aggregates, external services, or non-trivial workflows.
- **Service classes** (`<Entity>Service.java`): Allowed for simple CRUD operations on a single aggregate where each method is straightforward.

**Rationale:** Creating separate UseCase classes for trivial CRUD operations adds unnecessary boilerplate without improving clarity. Services provide a pragmatic grouping for related operations.

**Example:**
```java
// UseCase: Complex orchestration
class PublishProjectUseCase {
    void execute(UUID projectId) {
        // Validate chapters exist
        // Update project status
        // Send notifications
        // Create audit log
    }
}

// Service: Simple CRUD aggregator
class ChapterService {
    List<Chapter> getByProjectId(UUID projectId);
    Chapter createOrUpdate(SaveChapterRequest request);
}
```

### ADR-006: Error Handling (RFC 7807)
All error responses MUST follow RFC 7807 (Problem Details for HTTP APIs).

**Implementation:**
- Use Spring Boot 3.x `ProblemDetail` class.
- Custom extension `"code"` for i18n error codes.
- Custom extension `"errors"` for field validation details.

**Response Format:**
```json
{
  "type": "about:blank",
  "title": "Validation Failed",
  "status": 400,
  "detail": "VALIDATION_FAILED",
  "instance": "/api/v1/chapters",
  "code": "VALIDATION_FAILED",
  "errors": [
    { "field": "title", "code": "VALIDATION_FIELD_REQUIRED" }
  ]
}
```

---

## Coding Standards Summary

| Concern | Rule |
|---------|------|
| DTOs | Java `record` - NO Lombok |
| JPA Entities | Lombok `@Getter/@Setter/@NoArgsConstructor` |
| Null Safety | `Optional` only in return types |
| Errors | RFC 7807 via `ProblemDetail` |
| Local Variables | Use `var` keyword |
