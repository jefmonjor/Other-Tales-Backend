# Other Tales Backend

Backend service for the **Other Tales** platform, built with **Spring Boot 3.4+** and **Java 21**, following **Hexagonal Architecture (Ports & Adapters)**.

## Tech Stack
- **Language:** Java 21
- **Framework:** Spring Boot 3.4.2
- **Database:** PostgreSQL 16 (Supabase managed)
- **Authentication:** OAuth2 Resource Server (Supabase Auth - JWT)
- **Migrations:** Flyway
- **Testing:** JUnit 5, Mockito, Testcontainers
- **Deployment:** Google Cloud Run (Docker)

## Architecture

The project follows a strict **Hexagonal Architecture**:

```
src/main/java/com/othertales
├── common/              # Shared components (Exceptions, Audit, Utils)
├── config/              # Spring Configuration (Security, OpenAPI)
└── modules/             # Feature Modules
    ├── identity/        # User Profiles, GDPR
    └── writing/         # Projects, Chapters, Content
        ├── application/ # UseCases, DTOs, Ports (Input/Output)
        ├── domain/      # Pure Java Domain Entities & Exceptions
        └── infrastructure/
            ├── persistence/ # JPA Repositories, Entities, Mappers
            └── web/         # REST Controllers
```

## Getting Started

### Prerequisites
- Java 21 SDK
- Maven 3.9+
- Docker (for Testcontainers)

### Running Locally
1. **Set Environment Variables:**
   ```bash
   export DB_URL=jdbc:postgresql://<HOST>:<PORT>/postgres
   export DB_USER=<USER>
   export DB_PASSWORD=<PASSWORD>
   export JWT_URI=https://<SUPABASE_REF>.supabase.co/auth/v1
   export JWKS_URI=https://<SUPABASE_REF>.supabase.co/rest/v1/rpc/jwks
   ```

2. **Build & Run:**
   ```bash
   mvn clean spring-boot:run
   ```

### Running Tests
```bash
mvn test
```

## Deployment
Deployed via Google Cloud Run.
Use the `deploy.sh` script (requires `gcloud` CLI authenticated).

```bash
./deploy.sh
```

## API Documentation
Once running, Swagger UI is available at:
`http://localhost:8080/swagger-ui.html`
