-- V3__migrate_users_to_profiles.sql
-- Migration to Supabase: Convert users table to profiles table
-- BLINDADO: Inyecta columnas faltantes para Trigger y índices

-- 1. RENOMBRADO IDEMPOTENTE
DO $$
BEGIN
    IF EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'users')
       AND NOT EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'profiles') THEN
        ALTER TABLE users RENAME TO profiles;
    ELSIF EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'users')
       AND EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'profiles') THEN
        DROP TABLE users CASCADE;
    END IF;
END $$;

-- 2. INYECCIÓN DE COLUMNAS FALTANTES (Para Trigger de Supabase e Índices)
DO $$
BEGIN
    -- Email (Necesario para índice y Trigger)
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='profiles' AND column_name='email') THEN
        ALTER TABLE profiles ADD COLUMN email TEXT;
    END IF;

    -- Full Name (Necesario para Trigger)
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='profiles' AND column_name='full_name') THEN
        ALTER TABLE profiles ADD COLUMN full_name TEXT;
    END IF;

    -- Avatar URL (Necesario para Trigger)
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='profiles' AND column_name='avatar_url') THEN
        ALTER TABLE profiles ADD COLUMN avatar_url TEXT;
    END IF;
END $$;

-- 3. GESTIÓN DE FOREIGN KEY (Projects -> Profiles)
DO $$
BEGIN
    IF EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'projects') THEN

        -- Limpiar FK vieja si existe
        IF EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_projects_user') THEN
            ALTER TABLE projects DROP CONSTRAINT fk_projects_user;
        END IF;

        -- Crear FK nueva apuntando a profiles
        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_projects_profile') THEN
            ALTER TABLE projects
            ADD CONSTRAINT fk_projects_profile
            FOREIGN KEY (user_id)
            REFERENCES profiles(id)
            ON DELETE CASCADE;
        END IF;
    END IF;
END $$;

-- 4. ÍNDICES (Ahora seguros porque inyectamos las columnas en el paso 2)
CREATE INDEX IF NOT EXISTS idx_profiles_email ON profiles(email);
