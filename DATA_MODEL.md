# Modelo de Datos (PostgreSQL - Supabase)

> **⚠️ SOURCE OF TRUTH:** Do not invent columns or tables not listed here.

## Authentication
Authentication is handled by **Supabase Auth** (`auth.users` table).
The backend acts as an **OAuth2 Resource Server** that validates JWTs issued by Supabase.

---

## Identity Module

### Table: `profiles` (schema: `public`)
| Column | Type | Constraints | Default | Description |
|--------|------|-------------|---------|-------------|
| `id` | UUID | PK | - | Matches `auth.users.id` from Supabase |
| `email` | VARCHAR | Unique, Not Null | - | User email |
| `full_name` | VARCHAR | - | - | Display name |
| `plan_type` | VARCHAR(20) | - | `'FREE'` | Enum: `FREE`, `PRO` |
| `terms_accepted` | BOOLEAN | - | `FALSE` | GDPR: Terms of Service |
| `privacy_accepted` | BOOLEAN | - | `FALSE` | GDPR: Privacy Policy |
| `marketing_accepted` | BOOLEAN | - | `FALSE` | GDPR: Marketing consent |
| `terms_accepted_at` | TIMESTAMPTZ | - | - | Timestamp of acceptance |
| `privacy_accepted_at` | TIMESTAMPTZ | - | - | Timestamp of acceptance |
| `marketing_accepted_at` | TIMESTAMPTZ | - | - | Timestamp of acceptance |
| `created_at` | TIMESTAMPTZ | - | `NOW()` | - |
| `updated_at` | TIMESTAMPTZ | - | `NOW()` | - |
| `version` | BIGINT | - | - | Optimistic Locking |

> **Note:** `password_hash` is no longer stored locally. Authentication is delegated to Supabase.

### Table: `consent_logs` (schema: `public`)
Audit trail for GDPR consent changes.

| Column | Type | Constraints | Default | Description |
|--------|------|-------------|---------|-------------|
| `id` | UUID | PK | `gen_random_uuid()` | - |
| `user_id` | UUID | FK → profiles.id, Not Null | - | ON DELETE CASCADE |
| `consent_type` | VARCHAR(50) | Not Null | - | `TERMS`, `PRIVACY`, `MARKETING` |
| `consent_given` | BOOLEAN | Not Null | `FALSE` | True if accepted |
| `ip_address` | VARCHAR(45) | - | - | Client IP |
| `user_agent` | VARCHAR(500) | - | - | Browser/client info |
| `recorded_at` | TIMESTAMPTZ | - | `NOW()` | - |

**Indexes:** `user_id`, `consent_type`, `recorded_at`

---

## Writing Module

### Table: `projects` (schema: `public`)
| Column | Type | Constraints | Default | Description |
|--------|------|-------------|---------|-------------|
| `id` | UUID | PK | - | - |
| `user_id` | UUID | FK → profiles.id, Index | - | Project owner |
| `title` | VARCHAR(255) | Not Null | - | - |
| `synopsis` | TEXT | - | - | - |
| `genre` | VARCHAR(100) | - | - | - |
| `current_word_count` | INTEGER | - | `0` | - |
| `target_word_count` | INTEGER | - | `50000` | - |
| `cover_url` | TEXT | - | - | Placeholder URL only |
| `status` | VARCHAR(20) | - | `'DRAFT'` | Enum: `DRAFT`, `PUBLISHED` |
| `deleted` | BOOLEAN | - | `FALSE` | Soft Delete flag |
| `created_at` | TIMESTAMPTZ | - | `NOW()` | - |
| `updated_at` | TIMESTAMPTZ | - | `NOW()` | - |
| `version` | BIGINT | - | - | Optimistic Locking |

**Indexes:** `user_id`, `deleted`

### Table: `chapters` (schema: `public`)
| Column | Type | Constraints | Default | Description |
|--------|------|-------------|---------|-------------|
| `id` | UUID | PK | `gen_random_uuid()` | - |
| `project_id` | UUID | FK → projects.id, Not Null | - | ON DELETE CASCADE |
| `title` | TEXT | Not Null | `'Untitled Chapter'` | Chapter title |
| `content` | TEXT | - | - | Rich text content |
| `order_index` | INTEGER | - | `0` | Sort order within project |
| `status` | VARCHAR(20) | - | `'DRAFT'` | Enum: `DRAFT`, `PUBLISHED` |
| `created_at` | TIMESTAMPTZ | - | `NOW()` | - |
| `updated_at` | TIMESTAMPTZ | - | `NOW()` | Auto-updated via trigger |

**Indexes:** `project_id`
**Triggers:** `handle_chapters_updated_at` → Updates `updated_at` on row change.

---

## Common Module

### Table: `app_audit_logs` (schema: `public`)
General application audit trail.

| Column | Type | Constraints | Default | Description |
|--------|------|-------------|---------|-------------|
| `id` | UUID | PK | `gen_random_uuid()` | - |
| `user_id` | UUID | FK → profiles.id | - | ON DELETE SET NULL |
| `action_type` | VARCHAR(100) | Not Null | - | e.g., `PROJECT_CREATED`, `CHAPTER_UPDATED` |
| `entity_id` | VARCHAR(100) | - | - | ID of affected entity |
| `details` | JSONB | - | `'{}'` | Additional context |
| `ip_address` | VARCHAR(45) | - | - | Client IP |
| `user_agent` | VARCHAR(500) | - | - | Browser/client info |
| `performed_at` | TIMESTAMPTZ | - | `NOW()` | - |

**Indexes:** `user_id`, `action_type`, `entity_id`, `performed_at`

---

## Migration History
| Version | Description |
|---------|-------------|
| V1 | Create users table (legacy) |
| V2 | Create projects table |
| V3 | Migrate users to profiles |
| V4 | Add consent_logs and app_audit_logs tables |
| V5-V9 | Schema fixes and column additions |
| V10 | Create chapters table with auto-update trigger |
