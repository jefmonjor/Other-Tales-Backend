-- V10__create_chapters_table.sql
-- Writing Module: Chapters table
-- Sincronización con Supabase (tabla ya creada manualmente)

-- 1. Crear función handle_updated_at si no existe
CREATE OR REPLACE FUNCTION public.handle_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 2. Crear tabla chapters
CREATE TABLE IF NOT EXISTS public.chapters (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES public.projects(id) ON DELETE CASCADE,
    title TEXT NOT NULL DEFAULT 'Untitled Chapter',
    content TEXT,
    order_index INTEGER DEFAULT 0,
    status VARCHAR(20) DEFAULT 'DRAFT',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 3. Crear índice en project_id
CREATE INDEX IF NOT EXISTS idx_chapters_project_id ON public.chapters(project_id);

-- 4. Crear trigger para updated_at
DROP TRIGGER IF EXISTS handle_chapters_updated_at ON public.chapters;

CREATE TRIGGER handle_chapters_updated_at
    BEFORE UPDATE ON public.chapters
    FOR EACH ROW
    EXECUTE FUNCTION public.handle_updated_at();
