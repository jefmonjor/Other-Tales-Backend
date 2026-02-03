# Contexto de Negocio: Other Tales

## Visión
Plataforma "All-in-one" para escritores. Gestión de proyectos, escritura offline y sincronización.

## Fases de Implementación (Backend Scope)

### Fase 1: Identity & Foundation
- **Authentication delegated to Supabase Auth** (OAuth2 Resource Server pattern).
- Perfiles de usuario (Plan `FREE` / `PRO`) stored in `public.profiles`.
- Backend validates JWTs issued by Supabase.

### Fase 2: Writing Core (Projects)
- Gestión de Proyectos (Novelas).
- Listado, Creación, Edición y Borrado (Soft Delete).
- Filtrado simple (Recientes, Título).

## Non-Goals (Explicitly Out of Scope for now)
- Real-time collaboration (CRDT, WebSockets, Yjs).
- Offline-first conflict resolution (beyond "Last Write Wins").
- Multi-tenant organizations.
- AI Analysis integration.
- S3 / File Storage implementation (Use URL placeholders).
