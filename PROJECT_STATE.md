# Project State & Progress

## Current Status
- **Phase:** 1 (Foundation)
- **Active Epic:** EPIC-1: Foundation & Infra setup
- **Architecture:** Hybrid Pivot - OAuth2 Resource Server with Supabase Auth

## Completed
- [x] Project Scaffolding
- [x] Docker Environment (Testcontainers for tests)
- [x] Writing Module (Projects CRUD)
- [x] **PIVOT: Migration to Supabase**
  - Removed local authentication (AuthController, UseCases, DTOs)
  - Configured OAuth2 Resource Server for JWT validation
  - Renamed `users` table to `profiles`
  - Updated entities to use Supabase schema

## Pending Decisions
- Profile synchronization strategy (when to create profiles from Supabase auth).
- JWT claims extraction for user context.

## Architecture Notes
- Authentication is delegated to Supabase Auth (`auth.users`).
- Backend validates JWTs using Supabase's JWKS endpoint.
- User profiles are stored in `public.profiles` and linked by UUID.
