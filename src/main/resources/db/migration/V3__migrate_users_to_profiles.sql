-- V3__migrate_users_to_profiles.sql
-- Migration to Supabase: Convert users table to profiles table
-- Authentication is now handled by Supabase Auth (auth.users)
-- BLINDADO: Idempotente y seguro para deploy limpio o re-deploy

DO $$
BEGIN
    -- ==========================================================================
    -- PASO 1: Renombrado Idempotente (users → profiles)
    -- ==========================================================================

    -- Case A: 'users' exists AND 'profiles' does NOT exist → RENAME
    IF EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'users')
       AND NOT EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'profiles')
    THEN
        ALTER TABLE users RENAME TO profiles;

    -- Case B: BOTH 'users' AND 'profiles' exist → DROP old 'users' to clean up
    ELSIF EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'users')
          AND EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'profiles')
    THEN
        DROP TABLE users CASCADE;

    -- Case C: Only 'profiles' exists → Nothing to do (already migrated)
    END IF;

    -- ==========================================================================
    -- PASO 2: Limpiar columna legacy (password_hash)
    -- ==========================================================================
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'profiles' AND column_name = 'password_hash'
    ) THEN
        ALTER TABLE profiles DROP COLUMN password_hash;
    END IF;

    -- ==========================================================================
    -- PASO 3: Crear FK de projects → profiles (AHORA que profiles existe)
    -- ==========================================================================
    IF EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'projects') THEN

        -- Eliminar FK vieja hacia 'users' si existe
        IF EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_projects_user') THEN
            ALTER TABLE projects DROP CONSTRAINT fk_projects_user;
        END IF;

        -- Crear FK correcta hacia 'profiles' si no existe
        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_projects_profile') THEN
            ALTER TABLE projects
            ADD CONSTRAINT fk_projects_profile
            FOREIGN KEY (user_id)
            REFERENCES profiles(id)
            ON DELETE CASCADE;
        END IF;
    END IF;

END $$;

-- ==========================================================================
-- PASO 4: Índices (fuera del bloque DO para mejor manejo de errores)
-- ==========================================================================
DROP INDEX IF EXISTS idx_users_email;
CREATE INDEX IF NOT EXISTS idx_profiles_email ON profiles(email);
