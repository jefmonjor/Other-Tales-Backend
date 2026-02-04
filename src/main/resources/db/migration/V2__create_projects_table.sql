-- V2__create_projects_table.sql
-- Writing Module: Projects table
-- BLINDADO: Maneja todos los casos de migración

-- 1. Crear la tabla si no existe (Estructura Base)
CREATE TABLE IF NOT EXISTS projects (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    genre VARCHAR(100),
    current_word_count INTEGER DEFAULT 0,
    target_word_count INTEGER DEFAULT 50000,
    cover_url TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 2. FIX CRÍTICO: Asegurar que la columna 'deleted' existe
-- Si la tabla ya existía pero sin esta columna, esto la añade sin romper nada.
ALTER TABLE projects ADD COLUMN IF NOT EXISTS deleted BOOLEAN DEFAULT FALSE;

-- 3. FIX ANTERIOR: Foreign Key a 'profiles' (NO users)
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_projects_user') THEN
        ALTER TABLE projects
        ADD CONSTRAINT fk_projects_user
        FOREIGN KEY (user_id)
        REFERENCES profiles(id)
        ON DELETE CASCADE;
    END IF;
END $$;

-- 4. Índices (Usando IF NOT EXISTS)
CREATE INDEX IF NOT EXISTS idx_projects_user_id ON projects(user_id);

-- Esta línea ahora funcionará porque aseguramos la columna en el paso 2
CREATE INDEX IF NOT EXISTS idx_projects_deleted ON projects(deleted);
