# API & DB CONTRACT — Other Tales Backend

> **Version:** 1.0.0 | **Fecha:** 2026-02-06 | **Estado:** DEFINITIVO
> **Audiencia:** Equipo Frontend, QA, DevOps
> **Base URL:** `https://<host>/api/v1`
> **Autenticación:** Bearer Token (Supabase JWT) en header `Authorization`

---

## TABLA DE CONTENIDOS

1. [AUDITORÍA DB: Schema SQL vs JPA](#1-auditoría-db-schema-sql-vs-jpa)
2. [CONTRATO DE API](#2-contrato-de-api)
3. [MODELO DE ERRORES](#3-modelo-de-errores)
4. [FLUJOS DE BORDE (Edge Cases)](#4-flujos-de-borde-edge-cases)
5. [BUGS ENCONTRADOS EN ESTA AUDITORÍA](#5-bugs-encontrados-en-esta-auditoría)

---

## 1. AUDITORÍA DB: Schema SQL vs JPA

### 1.1 Tabla `profiles`

| Columna SQL | Tipo SQL | Campo JPA | Tipo JPA | Estado |
|---|---|---|---|---|
| `id` | UUID PK | `id` | UUID `@Id` | OK |
| `email` | TEXT | `email` | String `@NotBlank @Email @Size(max=255)` | WARN: JPA mas restrictivo que DB |
| `full_name` | TEXT | `fullName` | String `@Column(name="full_name")` | OK |
| `avatar_url` | TEXT | `avatarUrl` | String `@Column(name="avatar_url")` | OK (FIXED) |
| `password_hash` | VARCHAR(255) | **NO EXISTE** | - | WARN: Columna muerta de V1 |
| `plan_type` | VARCHAR(50) DEFAULT 'FREE' | `planType` | `PlanTypeEntity` enum (STRING) | OK |
| `terms_accepted` | BOOLEAN DEFAULT false | `termsAccepted` | boolean | OK (entity only) |
| `terms_accepted_at` | TIMESTAMPTZ | `termsAcceptedAt` | Instant | OK (entity only) |
| `privacy_accepted` | BOOLEAN DEFAULT false | `privacyAccepted` | boolean | OK (entity only) |
| `privacy_accepted_at` | TIMESTAMPTZ | `privacyAcceptedAt` | Instant | OK (entity only) |
| `marketing_accepted` | BOOLEAN DEFAULT false | `marketingAccepted` | boolean | OK (entity only) |
| `marketing_accepted_at` | TIMESTAMPTZ | `marketingAcceptedAt` | Instant | OK (entity only) |
| `created_at` | TIMESTAMPTZ DEFAULT NOW() | `createdAt` | Instant | OK |
| `updated_at` | TIMESTAMPTZ DEFAULT NOW() | `updatedAt` | Instant | OK |
| `version` | BIGINT DEFAULT 0 | `version` | Long `@Version` | OK |

### 1.2 Tabla `projects`

| Columna SQL | Tipo SQL | Campo JPA | Tipo JPA | Estado |
|---|---|---|---|---|
| `id` | UUID PK | `id` | UUID `@Id` | OK |
| `user_id` | UUID NOT NULL FK | `userId` | UUID `@NotNull` | OK |
| `title` | VARCHAR(255) NOT NULL | `title` | String `@NotBlank @Size(1,255)` | OK |
| `synopsis` | VARCHAR(2000) | `synopsis` | String `@Size(max=2000)` | OK |
| `genre` | VARCHAR(100) | `genre` | String `@Size(max=100)` | OK |
| `current_word_count` | INTEGER DEFAULT 0 | `currentWordCount` | int `@Min(0)` | OK |
| `target_word_count` | INTEGER DEFAULT 50000 | `targetWordCount` | int `@Min(1)` | OK |
| `cover_url` | TEXT | `coverUrl` | String `@Size(max=500)` | WARN: JPA limita a 500 chars |
| `status` | VARCHAR(20) DEFAULT 'DRAFT' | `status` | `ProjectStatusEntity` enum | OK |
| `deleted` | BOOLEAN DEFAULT FALSE | `deleted` | boolean | OK |
| `created_at` | TIMESTAMPTZ DEFAULT NOW() | `createdAt` | Instant | OK |
| `updated_at` | TIMESTAMPTZ DEFAULT NOW() | `updatedAt` | Instant | OK |
| `version` | BIGINT DEFAULT 0 | `version` | Long `@Version` | OK |

### 1.3 Tabla `chapters`

| Columna SQL | Tipo SQL | Campo JPA | Tipo JPA | Estado |
|---|---|---|---|---|
| `id` | UUID PK DEFAULT gen_random_uuid() | `id` | UUID `@Id` | OK (app genera UUID) |
| `project_id` | UUID NOT NULL FK | `project` | `ProjectEntity @ManyToOne(LAZY)` | OK (JoinColumn) |
| `title` | TEXT NOT NULL DEFAULT 'Untitled Chapter' | `title` | String `@NotBlank @Size(max=255)` | WARN: JPA limita a 255 chars |
| `content` | TEXT | `content` | String `columnDefinition="TEXT"` | OK |
| `order_index` | INTEGER DEFAULT 0 | `orderIndex` | int `@Column(name="order_index")` | OK |
| `status` | VARCHAR(20) DEFAULT 'DRAFT' | `status` | `ChapterStatusEntity` enum (STRING) | OK |
| `created_at` | TIMESTAMPTZ DEFAULT NOW() | `createdAt` | Instant | OK |
| `updated_at` | TIMESTAMPTZ DEFAULT NOW() | `updatedAt` | Instant | OK |
| `version` | BIGINT DEFAULT 0 | `version` | Long `@Version` | OK |

### 1.4 Tabla `consent_logs`

| Columna SQL | Tipo SQL | Campo JPA | Tipo JPA | Estado |
|---|---|---|---|---|
| `id` | UUID PK DEFAULT gen_random_uuid() | `id` | UUID `@GeneratedValue(UUID)` | OK |
| `user_id` | UUID NOT NULL FK | `userId` | UUID | OK |
| `consent_type` | VARCHAR(50) NOT NULL | `consentType` | `ConsentType` enum (STRING) | OK |
| `consent_given` | BOOLEAN DEFAULT FALSE | `consentGiven` | boolean | OK |
| `ip_address` | VARCHAR(45) | `ipAddress` | String | OK |
| `user_agent` | VARCHAR(500) | `userAgent` | String | OK |
| `recorded_at` | TIMESTAMPTZ DEFAULT NOW() | `recordedAt` | Instant | OK |

### 1.5 Tabla `app_audit_logs`

| Columna SQL | Tipo SQL | Campo JPA | Tipo JPA | Estado |
|---|---|---|---|---|
| `id` | UUID PK DEFAULT gen_random_uuid() | `id` | UUID `@GeneratedValue(UUID)` | OK |
| `user_id` | UUID FK (SET NULL) | `userId` | UUID | OK |
| `action_type` | VARCHAR(100) NOT NULL | `actionType` | String | OK |
| `entity_id` | VARCHAR(100) | `entityId` | String | OK |
| `details` | JSONB DEFAULT '{}' | `details` | `Map<String,Object>` `@JdbcTypeCode(JSON)` | OK |
| `ip_address` | VARCHAR(45) | `ipAddress` | String | OK |
| `user_agent` | VARCHAR(500) | `userAgent` | String | OK |
| `performed_at` | TIMESTAMPTZ DEFAULT NOW() | `performedAt` | Instant | OK |

### 1.6 Discrepancias Detectadas

#### 1.6.1 `avatar_url` — CORREGIDO

| Severidad | Descripcion |
|---|---|
| **RESUELTO** | Campo `avatarUrl` agregado a `ProfileEntity`, `Profile` (dominio), `ProfileMapper`, y `ProfileResponse`. Ahora `GET /profiles/me` devuelve el avatar sincronizado desde Supabase. |

#### 1.6.2 `password_hash` — Columna muerta

| Severidad | Descripcion |
|---|---|
| **BAJA** | Residuo de V1 (`users` table). Supabase maneja auth; esta columna está obsoleta. No afecta al Frontend. |

#### 1.6.3 `cover_url` — Limite JPA vs SQL

| Severidad | Descripcion |
|---|---|
| **BAJA** | SQL define `TEXT` (ilimitado), JPA valida `@Size(max=500)`. URLs de imagenes mayores a 500 caracteres seran rechazadas por validacion JPA con error `VALIDATION_FIELD_INVALID`. |

#### 1.6.4 Chapter `title` — Limite JPA vs SQL

| Severidad | Descripcion |
|---|---|
| **BAJA** | SQL define `TEXT` (ilimitado), JPA valida `@Size(max=255)`. Titulos mayores a 255 caracteres seran rechazados. |

---

## 2. CONTRATO DE API

### 2.0 Autenticacion

Todos los endpoints bajo `/api/**` requieren:

```
Authorization: Bearer <supabase_jwt_token>
```

El backend extrae `userId` del claim `sub` del JWT. **No existe header `X-User-Id`.**

**Endpoints publicos** (sin token):
- `GET /` — Health check
- `GET /actuator/health` — Health check (Cloud Run)
- `GET /actuator/info` — App info
- `GET /swagger-ui/**` — OpenAPI UI
- `GET /v3/api-docs/**` — OpenAPI JSON

---

### 2.1 Identity Module

#### `GET /api/v1/profiles/me` — Obtener perfil del usuario autenticado

**Request:**
```
GET /api/v1/profiles/me
Authorization: Bearer <token>
```

No body. No query params.

**Response 200:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "email": "user@example.com",
  "fullName": "John Doe",
  "avatarUrl": "https://lh3.googleusercontent.com/a/example",
  "planType": "FREE",
  "termsAccepted": true,
  "privacyAccepted": true,
  "marketingAccepted": false,
  "createdAt": "2026-01-15T10:30:00Z"
}
```

| Campo | Tipo | Nullable | Descripcion |
|---|---|---|---|
| `id` | UUID | No | ID del usuario (= Supabase auth.users.id) |
| `email` | string | No | Email del usuario |
| `fullName` | string | **Si** | Nombre completo (puede ser null si no se ha configurado) |
| `avatarUrl` | string | **Si** | URL del avatar (sincronizado desde OAuth provider por Supabase) |
| `planType` | string | No | `"FREE"` o `"PRO"` |
| `termsAccepted` | boolean | No | Si el usuario acepto los terminos de servicio |
| `privacyAccepted` | boolean | No | Si el usuario acepto la politica de privacidad |
| `marketingAccepted` | boolean | No | Si el usuario acepto comunicaciones de marketing |
| `createdAt` | ISO 8601 | No | Fecha de creacion de la cuenta |

**Errores posibles:**

| HTTP | Code | Cuando |
|---|---|---|
| 401 | `AUTH_INVALID_TOKEN` | JWT invalido o expirado |
| 404 | `PROFILE_NOT_FOUND` | Usuario autenticado sin perfil en BD |

---

#### `POST /api/v1/user/consent` — Actualizar consentimiento GDPR

**Request:**
```
POST /api/v1/user/consent
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "consentType": "TERMS_OF_SERVICE",
  "granted": true
}
```

| Campo | Tipo | Obligatorio | Valores permitidos |
|---|---|---|---|
| `consentType` | string (enum) | **Si** | `"TERMS_OF_SERVICE"`, `"PRIVACY_POLICY"`, `"MARKETING_COMMUNICATIONS"` |
| `granted` | boolean | **Si** | `true` / `false` |

**Response 200:**
```json
{
  "consentType": "TERMS_OF_SERVICE",
  "granted": true,
  "recordedAt": "2026-02-06T14:30:00Z"
}
```

| Campo | Tipo | Nullable | Descripcion |
|---|---|---|---|
| `consentType` | string | No | Tipo de consentimiento actualizado |
| `granted` | boolean | No | Nuevo estado del consentimiento |
| `recordedAt` | ISO 8601 | No | Timestamp del registro |

**Errores posibles:**

| HTTP | Code | Cuando |
|---|---|---|
| 400 | `VALIDATION_FAILED` | `consentType` o `granted` faltante/invalido |
| 401 | `AUTH_INVALID_TOKEN` | JWT invalido |
| 404 | `PROFILE_NOT_FOUND` | Usuario sin perfil en BD |

---

### 2.2 Writing Module — Projects

#### `POST /api/v1/projects` — Crear proyecto

**Request:**
```json
{
  "title": "Mi Novela",
  "synopsis": "Una historia epica...",
  "genre": "Fantasy",
  "targetWordCount": 80000
}
```

| Campo | Tipo | Obligatorio | Validacion |
|---|---|---|---|
| `title` | string | **Si** | 1-255 chars, patron: `^[\p{L}\p{N}\s.,!?'":;()-]+$` |
| `synopsis` | string | No | Max 2000 chars |
| `genre` | string | No | Max 100 chars, patron: `^[\p{L}\p{N}\s-]*$` |
| `targetWordCount` | integer | No | Min 1. Default: `50000` |

**Response 201:**
```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "title": "Mi Novela",
  "synopsis": "Una historia epica...",
  "genre": "Fantasy",
  "currentWordCount": 0,
  "targetWordCount": 80000,
  "coverUrl": null,
  "status": "DRAFT",
  "createdAt": "2026-02-06T14:30:00Z",
  "updatedAt": "2026-02-06T14:30:00Z"
}
```

**Errores posibles:**

| HTTP | Code | Cuando |
|---|---|---|
| 400 | `VALIDATION_FAILED` | Campos invalidos (ver tabla de validacion) |
| 400 | `PROJECT_INVALID_TITLE` | Titulo vacio (validacion de dominio) |
| 400 | `PROJECT_INVALID_WORD_COUNT` | targetWordCount < 1 (validacion de dominio) |
| 401 | `AUTH_INVALID_TOKEN` | JWT invalido |

---

#### `GET /api/v1/projects` — Listar proyectos del usuario

**Query Params:**

| Param | Tipo | Default | Valores |
|---|---|---|---|
| `page` | int | `0` | 0-based page index |
| `size` | int | `20` | Items per page |
| `sortBy` | string | `null` (= `updated` DESC) | `title`, `title_desc`, `created`, `created_asc`, `updated_asc` |

**Response 200:**
```json
{
  "content": [
    {
      "id": "a1b2c3d4-...",
      "title": "Mi Novela",
      "genre": "Fantasy",
      "currentWordCount": 1500,
      "targetWordCount": 80000,
      "status": "DRAFT",
      "updatedAt": "2026-02-06T14:30:00Z"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1
}
```

| Campo (content[]) | Tipo | Nullable | Descripcion |
|---|---|---|---|
| `id` | UUID | No | |
| `title` | string | No | |
| `genre` | string | **Si** | |
| `currentWordCount` | int | No | |
| `targetWordCount` | int | No | |
| `status` | string | No | `"DRAFT"` o `"PUBLISHED"` |
| `updatedAt` | ISO 8601 | No | |

> **NOTA:** Este endpoint solo devuelve proyectos **no eliminados** (`deleted = false`).
> El `sortBy` por defecto ordena por `updated_at` descendente (mas reciente primero).

---

#### `GET /api/v1/projects/{projectId}` — Obtener proyecto por ID

**Response 200:** Mismo formato que `ProjectResponse` (ver POST response).

**Errores:**

| HTTP | Code | Cuando |
|---|---|---|
| 404 | `PROJECT_NOT_FOUND` | No existe, esta eliminado, o pertenece a otro usuario |

---

#### `PUT /api/v1/projects/{projectId}` — Actualizar proyecto

**Request:** (Actualizacion parcial — solo enviar campos a modificar)
```json
{
  "title": "Nuevo Titulo",
  "targetWordCount": 100000
}
```

| Campo | Tipo | Obligatorio | Validacion |
|---|---|---|---|
| `title` | string | No | 1-255 chars (mismas reglas que crear) |
| `synopsis` | string | No | Max 2000 chars |
| `genre` | string | No | Max 100 chars |
| `targetWordCount` | integer | No | Min 1 |

> **IMPORTANTE:** Campos enviados como `null` o no incluidos **no se modifican**.
> Para "vaciar" un campo como `synopsis`, enviar string vacio `""`.

**Response 200:** `ProjectResponse` completo con datos actualizados.

**Errores:**

| HTTP | Code | Cuando |
|---|---|---|
| 400 | `VALIDATION_FAILED` | Campos invalidos |
| 400 | `PROJECT_INVALID_TITLE` | Titulo vacio en dominio |
| 401 | `AUTH_INVALID_TOKEN` | JWT invalido |
| 404 | `PROJECT_NOT_FOUND` | No existe o no es del usuario |

---

#### `DELETE /api/v1/projects/{projectId}` — Eliminar proyecto (soft delete)

**Response 204:** Sin body.

> **IMPORTANTE:** Es soft delete. El proyecto se marca como `deleted = true`.
> Desaparece de los listados pero sigue en BD.
> Los capitulos del proyecto **NO se eliminan** — siguen en BD vinculados al proyecto.

**Errores:**

| HTTP | Code | Cuando |
|---|---|---|
| 401 | `AUTH_INVALID_TOKEN` | JWT invalido |
| 404 | `PROJECT_NOT_FOUND` | No existe o no es del usuario |

---

### 2.3 Writing Module — Chapters

#### `POST /api/v1/projects/{projectId}/chapters` — Crear capitulo

**Path Param:** `projectId` (UUID) — Proyecto al que pertenece.

**Request:**
```json
{
  "title": "Capitulo 1: El Inicio",
  "content": "Era una noche oscura...",
  "sortOrder": 0
}
```

| Campo | Tipo | Obligatorio | Validacion |
|---|---|---|---|
| `title` | string | **Si** | 1-255 chars |
| `content` | string | No | Default: `""` (string vacio) |
| `sortOrder` | integer | No | Auto-calculado si null (= siguiente indice) |

**Response 201:**
```json
{
  "id": "b2c3d4e5-...",
  "projectId": "a1b2c3d4-...",
  "title": "Capitulo 1: El Inicio",
  "content": "Era una noche oscura...",
  "sortOrder": 0,
  "wordCount": 5,
  "createdAt": "2026-02-06T15:00:00Z",
  "updatedAt": "2026-02-06T15:00:00Z"
}
```

| Campo | Tipo | Nullable | Descripcion |
|---|---|---|---|
| `id` | UUID | No | ID del capitulo |
| `projectId` | UUID | No | ID del proyecto padre |
| `title` | string | No | Titulo del capitulo |
| `content` | string | No | Contenido (puede ser `""`) |
| `sortOrder` | int | No | Indice de orden (0-based) |
| `wordCount` | int | No | Conteo de palabras calculado dinamicamente |
| `createdAt` | ISO 8601 | No | |
| `updatedAt` | ISO 8601 | No | |

**Errores:**

| HTTP | Code | Cuando |
|---|---|---|
| 400 | `VALIDATION_FAILED` | Titulo faltante o invalido |
| 401 | `AUTH_INVALID_TOKEN` | JWT invalido |
| 404 | `PROJECT_NOT_FOUND` | Proyecto no existe o no es del usuario |

---

#### `GET /api/v1/projects/{projectId}/chapters` — Listar capitulos de un proyecto

**Response 200:** Array de `ChapterResponse`, ordenado por `sortOrder` ascendente.
```json
[
  {
    "id": "b2c3d4e5-...",
    "projectId": "a1b2c3d4-...",
    "title": "Capitulo 1: El Inicio",
    "content": "Era una noche oscura...",
    "sortOrder": 0,
    "wordCount": 5,
    "createdAt": "2026-02-06T15:00:00Z",
    "updatedAt": "2026-02-06T15:00:00Z"
  }
]
```

> **NOTA:** Devuelve array vacio `[]` si el proyecto no tiene capitulos.

**Errores:**

| HTTP | Code | Cuando |
|---|---|---|
| 401 | `AUTH_INVALID_TOKEN` | JWT invalido |
| 403 | `CHAPTER_ACCESS_DENIED` | Proyecto existe pero no es del usuario |

---

#### `GET /api/v1/chapters/{chapterId}` — Obtener capitulo por ID

**Response 200:** `ChapterResponse` (mismo formato que arriba).

**Errores:**

| HTTP | Code | Cuando |
|---|---|---|
| 401 | `AUTH_INVALID_TOKEN` | JWT invalido |
| 403 | `CHAPTER_ACCESS_DENIED` | Capitulo existe pero el proyecto no es del usuario |
| 404 | `CHAPTER_NOT_FOUND` | Capitulo no existe |

---

#### `PUT /api/v1/chapters/{chapterId}` — Actualizar capitulo

**Request:** (Actualizacion parcial)
```json
{
  "title": "Capitulo 1: El Verdadero Inicio",
  "content": "Era una noche oscura y tormentosa..."
}
```

| Campo | Tipo | Obligatorio | Validacion |
|---|---|---|---|
| `title` | string | No | 1-255 chars (si se envia) |
| `content` | string | No | Sin limite |

> **NOTA:** Campos `null` o no incluidos no se modifican.
> `wordCount` se recalcula automaticamente al actualizar `content`.

**Response 200:** `ChapterResponse` actualizado.

**Errores:**

| HTTP | Code | Cuando |
|---|---|---|
| 400 | `VALIDATION_FAILED` | Titulo invalido |
| 401 | `AUTH_INVALID_TOKEN` | JWT invalido |
| 403 | `CHAPTER_ACCESS_DENIED` | Proyecto no es del usuario |
| 404 | `CHAPTER_NOT_FOUND` | Capitulo no existe |

---

#### `DELETE /api/v1/chapters/{chapterId}` — Eliminar capitulo (hard delete)

**Response 204:** Sin body.

> **IMPORTANTE:** Es eliminacion FISICA (hard delete). El capitulo desaparece permanentemente.
> Contrasta con proyectos que usan soft delete.

**Errores:**

| HTTP | Code | Cuando |
|---|---|---|
| 401 | `AUTH_INVALID_TOKEN` | JWT invalido |
| 403 | `CHAPTER_ACCESS_DENIED` | Proyecto no es del usuario |
| 404 | `CHAPTER_NOT_FOUND` | Capitulo no existe |

---

## 3. MODELO DE ERRORES

### 3.1 Formato RFC 7807 (ProblemDetail)

**TODOS** los errores devuelven este formato:

```json
{
  "type": "about:blank",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "PROJECT_NOT_FOUND",
  "instance": "/api/v1/projects/a1b2c3d4-...",
  "code": "PROJECT_NOT_FOUND"
}
```

| Campo | Tipo | Descripcion |
|---|---|---|
| `type` | string | Siempre `"about:blank"` |
| `title` | string | Titulo legible del error |
| `status` | int | HTTP status code |
| `detail` | string | Codigo del error (machine-readable) |
| `instance` | string | URI del request que causo el error |
| `code` | string | **CLAVE PARA FRONTEND**: Codigo unico para i18n |

### 3.2 Errores de Validacion

Para `VALIDATION_FAILED`, se incluye campo extra `errors`:

```json
{
  "type": "about:blank",
  "title": "Validation Failed",
  "status": 400,
  "detail": "VALIDATION_FAILED",
  "instance": "/api/v1/projects",
  "code": "VALIDATION_FAILED",
  "errors": [
    { "field": "title", "code": "VALIDATION_FIELD_REQUIRED" },
    { "field": "targetWordCount", "code": "VALIDATION_FIELD_INVALID" }
  ]
}
```

### 3.3 Catalogo Completo de Codigos

| Codigo | HTTP | Descripcion |
|---|---|---|
| **Auth** | | |
| `AUTH_INVALID_TOKEN` | 401 | JWT invalido, malformado o expirado |
| `AUTH_TOKEN_EXPIRED` | 401 | JWT expirado (subcase) |
| `AUTH_UNAUTHORIZED` | 403 | Acceso denegado por Spring Security |
| **Profile** | | |
| `PROFILE_NOT_FOUND` | 404 | Perfil no existe en BD |
| `PROFILE_EMAIL_EXISTS` | 409 | Email duplicado (conflict) |
| `PROFILE_INVALID_EMAIL` | 400 | Formato de email invalido |
| `PROFILE_INVALID_NAME` | 400 | Nombre con caracteres no permitidos |
| **Project** | | |
| `PROJECT_NOT_FOUND` | 404 | Proyecto no existe, eliminado, o de otro usuario |
| `PROJECT_INVALID_TITLE` | 400 | Titulo vacio (validacion dominio) |
| `PROJECT_INVALID_GENRE` | 400 | Genero con caracteres invalidos |
| `PROJECT_INVALID_WORD_COUNT` | 400 | targetWordCount < 1 |
| `PROJECT_ACCESS_DENIED` | 403 | Proyecto no pertenece al usuario |
| **Chapter** | | |
| `CHAPTER_NOT_FOUND` | 404 | Capitulo no existe |
| `CHAPTER_ACCESS_DENIED` | 403 | Capitulo pertenece a proyecto de otro usuario |
| **Validation** | | |
| `VALIDATION_FAILED` | 400 | Error de validacion Jakarta (campos) |
| `VALIDATION_FIELD_REQUIRED` | 400 | Campo obligatorio faltante (en array `errors`) |
| `VALIDATION_FIELD_INVALID` | 400 | Campo con formato invalido (en array `errors`) |
| **Generic** | | |
| `DATA_CONFLICT` | 409 | Conflicto de integridad de datos |
| `INTERNAL_ERROR` | 500 | Error interno no manejado |

---

## 4. FLUJOS DE BORDE (Edge Cases)

### 4.1 Cascadas en Base de Datos

```
profiles --[ON DELETE CASCADE]--> projects --[ON DELETE CASCADE]--> chapters
profiles --[ON DELETE CASCADE]--> consent_logs
profiles --[ON DELETE SET NULL]--> app_audit_logs
```

| Accion | Efecto en BD | Efecto en API |
|---|---|---|
| **Eliminar perfil en Supabase** | CASCADE borra projects, chapters, consent_logs. audit_logs pone user_id = NULL | El usuario pierde TODO. Irreversible. |
| **DELETE /projects/{id}** | Soft delete (`deleted = true`). Chapters permanecen. | Proyecto desaparece de listados. Chapters quedan huerfanos (ver 4.2). |
| **DELETE /chapters/{id}** | Hard delete fisico (DELETE FROM chapters) | Capitulo desaparece permanentemente. |

### 4.2 Capitulos Huerfanos (tras soft-delete de proyecto)

**Escenario:** Frontend elimina un proyecto (soft delete). Los capitulos siguen existiendo en BD.

**Comportamiento actual:**
- `GET /projects/{projectId}/chapters` devolvera `CHAPTER_ACCESS_DENIED` (403) porque `existsByIdAndUserId` filtra por `deleted = false`.
- `GET /chapters/{chapterId}` devolvera `CHAPTER_ACCESS_DENIED` (403) por la misma razon.
- Los capitulos son **inaccesibles via API** pero **ocupan espacio en BD**.

**Recomendacion Frontend:** Tras eliminar un proyecto, no intentar acceder a sus capitulos.

### 4.3 Soft Delete: Lo que el Frontend NO ve

El endpoint `GET /projects` **automaticamente excluye** proyectos con `deleted = true`. No hay forma via API de:
- Ver proyectos eliminados
- Restaurar proyectos eliminados
- Saber que un proyecto fue eliminado (devuelve 404, no 410 Gone)

### 4.4 Word Count: Calculado vs Almacenado

| Entidad | Campo | Comportamiento |
|---|---|---|
| **Chapter** | `wordCount` | **Calculado dinamicamente** en cada request (`content.split("\\s+").length`). No se almacena en BD. |
| **Project** | `currentWordCount` | **Almacenado en BD**. Debe actualizarse manualmente (no hay auto-sync con capitulos). |

**Implicacion Frontend:** El `currentWordCount` de un proyecto **NO se actualiza automaticamente** cuando se editan capitulos. Si el Frontend necesita un total actualizado, debe calcularlo sumando los `wordCount` de todos los capitulos.

### 4.5 Valores por Defecto

| Campo | Default Backend | Cuando aplica |
|---|---|---|
| `targetWordCount` | `50000` | Si no se envia en `CreateProjectRequest` |
| Chapter `content` | `""` (string vacio) | Si no se envia o es null en `CreateChapterRequest` |
| Chapter `title` | `"Untitled Chapter"` | Si se envia blank (solo en dominio, DTO requiere `@NotBlank`) |
| Chapter `sortOrder` | Auto-calculado (max + 1) | Si no se envia en `CreateChapterRequest` |
| Project `status` | `"DRAFT"` | Siempre al crear |
| Project `currentWordCount` | `0` | Siempre al crear |

### 4.6 Optimistic Locking (`@Version`)

Las entidades `profiles`, `projects`, y `chapters` usan optimistic locking con campo `version`.

**Comportamiento:** Si dos requests intentan actualizar la misma entidad simultaneamente, el segundo recibe:

```json
{
  "type": "about:blank",
  "title": "Data Conflict",
  "status": 409,
  "code": "DATA_CONFLICT"
}
```

**Accion Frontend:** Reintentar la operacion (re-leer y re-enviar).

> **NOTA:** El campo `version` NO se expone en ninguna response. El conflicto se detecta internamente.

### 4.7 Trigger de BD en `chapters.updated_at`

La tabla `chapters` tiene un trigger SQL `handle_chapters_updated_at` que ejecuta `NEW.updated_at = NOW()` en cada UPDATE. Esto ocurre **ademas** de que JPA establece el campo desde el dominio.

**Consecuencia:** El `updatedAt` devuelto en la response podria diferir ligeramente (milisegundos) del valor final en BD, ya que el trigger sobreescribe el valor.

### 4.8 Paginacion

| Param | Tipo | Default | Descripcion |
|---|---|---|---|
| `page` | int | `0` | **0-indexed**. Primera pagina es `0`, no `1`. |
| `size` | int | `20` | Items por pagina. **Sin limite maximo** en codigo (puede enviar 1000). |
| `sortBy` | string | `null` | Ver tabla de valores validos abajo. |

**Valores validos de `sortBy`:**

| Valor | Orden resultante |
|---|---|
| (null / vacio) | `updated_at` DESC (default) |
| `title` | `title` ASC |
| `title_desc` | `title` DESC |
| `created` | `created_at` DESC |
| `created_asc` | `created_at` ASC |
| `updated_asc` | `updated_at` ASC |
| (cualquier otro) | `updated_at` DESC (fallback) |

### 4.9 Autorizacion: Aislamiento por Usuario

- Un usuario **SOLO** puede ver/editar/eliminar sus propios proyectos y capitulos.
- Intentar acceder al recurso de otro usuario devuelve `404 PROJECT_NOT_FOUND` (no `403`) para no filtrar existencia.
- Para capitulos, se devuelve `403 CHAPTER_ACCESS_DENIED` si el capitulo existe pero el proyecto no es del usuario.

### 4.10 CORS

| Config | Valor |
|---|---|
| Origenes permitidos | `localhost:3000`, `localhost:5173`, `other-tales.vercel.app`, `*.vercel.app` |
| Metodos | GET, POST, PUT, PATCH, DELETE, OPTIONS |
| Headers permitidos | Authorization, Content-Type, X-Requested-With, Accept, Origin, X-User-Id |
| Headers expuestos | X-Total-Count, X-Page-Size, X-Current-Page |
| Credentials | Habilitado |
| Preflight cache | 3600s (1 hora) |

---

## 5. BUGS ENCONTRADOS Y CORREGIDOS EN ESTA AUDITORÍA

### 5.1 ~~BUG CRITICO: `ChapterJpaAdapter.save()` — isNew siempre true~~ CORREGIDO

**Ubicacion:** `ChapterJpaAdapter.java` + `ChapterMapper.java`

**Problema:** `ChapterMapper.toEntity()` siempre creaba `new ChapterEntity()`, con `isNew = true`. A diferencia de `ProjectJpaAdapter` y `ProfileJpaAdapter` (ya corregidos), `ChapterJpaAdapter` no buscaba la entidad existente.

**Fix aplicado:**
- `ChapterMapper`: Agregado overload `toEntity(Chapter, ProjectEntity, ChapterEntity)` que reutiliza la entidad existente
- `ChapterJpaAdapter.save()`: Ahora busca entidad existente con `findById()` antes de mapear

### 5.2 ~~BUG CRITICO: Consent Update no muta el perfil~~ CORREGIDO

**Ubicacion:** `Profile.java` + `ProfileMapper.java` + `UpdateConsentUseCase.java`

**Problema:** El dominio `Profile` no tenia campos de consentimiento. `UpdateConsentUseCase` guardaba el perfil sin mutar nada.

**Fix aplicado:**
- `Profile` (dominio): Agregados campos `termsAccepted`, `termsAcceptedAt`, `privacyAccepted`, `privacyAcceptedAt`, `marketingAccepted`, `marketingAcceptedAt` + metodos `updateConsent()` y `getConsentValue()`
- `ProfileMapper`: Ahora mapea todos los campos de consentimiento bidirecionalmente
- `UpdateConsentUseCase`: Ahora llama `profile.updateConsent()` con mutacion real + lee `previousValue` real

### 5.3 ~~BUG MEDIO: `avatarUrl` no mapeado~~ CORREGIDO

**Ubicacion:** `ProfileEntity.java` + `Profile.java` + `ProfileMapper.java` + `ProfileResponse.java`

**Fix aplicado:**
- `ProfileEntity`: Agregado campo `avatarUrl` con `@Column(name = "avatar_url")`
- `Profile` (dominio): Agregado campo `avatarUrl` en `create()` y `reconstitute()`
- `ProfileMapper`: Mapea `avatarUrl` bidirecionalmente
- `ProfileResponse`: Agregado campo `avatarUrl`
- `GetCurrentProfileUseCase`: Incluye `avatarUrl` en la respuesta

---

*Documento generado automaticamente por auditoria de codigo el 2026-02-06. Bugs corregidos en el mismo commit.*
