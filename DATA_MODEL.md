# Modelo de Datos (PostgreSQL - Supabase)

> **⚠️ SOURCE OF TRUTH:** Do not invent columns or tables not listed here.

## Authentication
Authentication is handled by **Supabase Auth** (`auth.users` table).
The backend acts as an **OAuth2 Resource Server** that validates JWTs issued by Supabase.

## Identity Module
**Table: `profiles`** (schema: `public`)
- `id` (UUID, PK) - Matches `auth.users.id` from Supabase
- `email` (VARCHAR, Unique, Not Null)
- `full_name` (VARCHAR)
- `plan_type` (ENUM: 'FREE', 'PRO') - Default: 'FREE'
- `created_at` (TIMESTAMPTZ)
- `updated_at` (TIMESTAMPTZ)
- `version` (BIGINT) - Optimistic Locking

> **Note:** `password_hash` is no longer stored locally. Authentication is delegated to Supabase.

## Writing Module
**Table: `projects`** (schema: `public`)
- `id` (UUID, PK)
- `user_id` (UUID, FK -> profiles.id, Index)
- `title` (VARCHAR(255), Not Null)
- `synopsis` (TEXT)
- `genre` (VARCHAR(100))
- `current_word_count` (INTEGER) - Default: 0
- `target_word_count` (INTEGER) - Default: 50000
- `cover_url` (VARCHAR) - Placeholder URL only
- `status` (ENUM: 'DRAFT', 'PUBLISHED')
- `deleted` (BOOLEAN) - Default: false (Soft Delete)
- `created_at`, `updated_at`, `version`
