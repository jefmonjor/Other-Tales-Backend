# Arquitectura del Sistema

## Estructura de Agentes (Roles Virtuales)
1. **@ARCHITECT:** Diseña contratos OpenAPI (`.yaml`) y Records Java (DTOs). No toca lógica.
2. **@CORE_DEV:** Implementa Dominio puro (DDD). Bounded Contexts y Casos de Uso.
3. **@INFRA_DEV:** "The Plumber". Conecta Spring Boot, JPA, Docker y Flyway.

## Decisiones Clave (ADR)
- **API First:** Definimos el contrato OpenAPI antes de escribir el Controller.
- **DB Schema:** Flyway es la única fuente de verdad para la estructura de la BD.
- **Database:** PostgreSQL hosted on Supabase.
- **Auth:** OAuth2 Resource Server - JWT validation against Supabase Auth.
  - Authentication is handled entirely by Supabase (auth.users).
  - Backend validates JWTs using Supabase's JWKS endpoint.
  - User profiles stored in `public.profiles` table.
