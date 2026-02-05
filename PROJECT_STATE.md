# Project State & Progress

## Current Status
- **Phase:** 1 (Foundation) - CLOSING
- **Active Epic:** EPIC-1: Foundation & Infra setup
- **Architecture:** Hybrid Pivot - OAuth2 Resource Server with Supabase Auth

---

## Completed Milestones

### Infrastructure
- [x] Project Scaffolding (Maven, Spring Boot 3.5.x)
- [x] Docker Environment (Testcontainers for tests)
- [x] Flyway Migrations (V1-V10)
- [x] Global Exception Handling (RFC 7807 ProblemDetails)

### Identity Module
- [x] **PIVOT: Migration to Supabase**
  - Removed local authentication (AuthController, UseCases, DTOs)
  - Configured OAuth2 Resource Server for JWT validation
  - Renamed `users` table to `profiles`
  - Updated entities to use Supabase schema
- [x] Profile CRUD endpoints
- [x] GDPR Consent Management
  - Consent fields on profiles table
  - `consent_logs` audit table
  - ConsentController endpoints

### Writing Module
- [x] Projects CRUD (Create, Read, Update, Soft Delete)
- [x] Chapters CRUD
  - ChapterEntity with ManyToOne to ProjectEntity
  - `GET /api/v1/projects/{projectId}/chapters`
  - `POST /api/v1/chapters` (create/update)
  - Ownership validation (403 on mismatch)

### Common Module
- [x] Audit Logging infrastructure (`app_audit_logs` table)
- [x] Error Codes centralized (`ErrorCodes.java`)
- [x] RFC 7807 compliant error responses

---

## Pending / Next Phase

### Phase 2: Writing Core (Upcoming)
- [ ] Rich text editor integration (content sync)
- [ ] Chapter reordering (drag & drop)
- [ ] Word count tracking per chapter
- [ ] Project statistics dashboard

### Pending Decisions
- Profile synchronization strategy (when to create profiles from Supabase auth)
- JWT claims extraction for user context (currently using `X-User-Id` header)

---

## Architecture Notes
- Authentication is delegated to Supabase Auth (`auth.users`).
- Backend validates JWTs using Supabase's JWKS endpoint.
- User profiles are stored in `public.profiles` and linked by UUID.
- Error responses follow RFC 7807 (ProblemDetails) standard.
- Domain Services allowed for simple CRUD; UseCases reserved for complex orchestration.

---

## Migration Log
| Date | Migration | Description |
|------|-----------|-------------|
| - | V1-V3 | Initial tables + Supabase pivot |
| - | V4 | GDPR consent + audit tables |
| - | V5-V9 | Schema fixes |
| - | V10 | Chapters table with auto-update trigger |
