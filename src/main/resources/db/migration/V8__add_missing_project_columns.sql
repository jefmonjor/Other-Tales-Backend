-- ============================================================================
-- V8: Add missing columns to projects table
-- ============================================================================
-- ISSUE: ProjectEntity expects synopsis, status, and version columns
-- FIX: Add all missing columns with appropriate defaults
-- ============================================================================

DO $$
BEGIN
    -- ========== SYNOPSIS ==========
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'projects'
          AND column_name = 'synopsis'
    ) THEN
        ALTER TABLE public.projects ADD COLUMN synopsis VARCHAR(2000);
        RAISE NOTICE 'Added column: synopsis';
    END IF;

    -- ========== STATUS (Enum: DRAFT, PUBLISHED) ==========
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'projects'
          AND column_name = 'status'
    ) THEN
        ALTER TABLE public.projects ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'DRAFT';
        RAISE NOTICE 'Added column: status';
    END IF;

    -- ========== VERSION (JPA Optimistic Locking) ==========
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'projects'
          AND column_name = 'version'
    ) THEN
        ALTER TABLE public.projects ADD COLUMN version BIGINT DEFAULT 0;
        RAISE NOTICE 'Added column: version';
    END IF;

END $$;

-- Index for status filtering (common query pattern)
CREATE INDEX IF NOT EXISTS idx_projects_status ON public.projects(status);
