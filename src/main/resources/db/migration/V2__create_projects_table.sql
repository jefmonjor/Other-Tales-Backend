-- V2__create_projects_table.sql
-- Writing Module: Projects table
-- BLINDADO: Maneja tabla existente sin columna 'deleted'

-- 1. Crear la tabla básica si no existe (Sin FK para evitar ciclos)
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

-- 2. BLOQUE DE SEGURIDAD: Inyectar columna 'deleted' si falta
-- Esto arregla el crash actual. Si la tabla ya existía, le metemos la columna.
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name='projects' AND column_name='deleted') THEN
        ALTER TABLE projects ADD COLUMN deleted BOOLEAN DEFAULT FALSE;
    END IF;
END $$;

-- 3. Índices (Ahora es seguro crearlos porque 'deleted' existe seguro)
CREATE INDEX IF NOT EXISTS idx_projects_user_id ON projects(user_id);
CREATE INDEX IF NOT EXISTS idx_projects_deleted ON projects(deleted);
