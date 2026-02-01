# Modelo de Datos (PostgreSQL 18)

> **⚠️ SOURCE OF TRUTH:** Do not invent columns or tables not listed here.

## Identity Module
**Table: `users`**
- `id` (UUID, PK)
- `email` (VARCHAR, Unique, Not Null)
- `password_hash` (VARCHAR, Not Null)
- `full_name` (VARCHAR)
- `plan_type` (ENUM: 'FREE', 'PRO') - Default: 'FREE'
- `created_at` (TIMESTAMPTZ)
- `updated_at` (TIMESTAMPTZ)
- `version` (BIGINT) - Optimistic Locking

## Writing Module
**Table: `projects`**
- `id` (UUID, PK)
- `user_id` (UUID, FK -> users.id, Index)
- `title` (VARCHAR(255), Not Null)
- `synopsis` (TEXT)
- `cover_url` (VARCHAR) - Placeholder URL only
- `status` (ENUM: 'DRAFT', 'PUBLISHED')
- `is_deleted` (BOOLEAN) - Default: false (Soft Delete)
- `created_at`, `updated_at`, `version`
