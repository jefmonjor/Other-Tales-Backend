-- V11__add_version_to_chapters.sql
-- AUDIT FIX #19 (FASE 4.3): Add optimistic locking column to chapters

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'chapters'
          AND column_name = 'version'
    ) THEN
        ALTER TABLE public.chapters ADD COLUMN version BIGINT DEFAULT 0;
        RAISE NOTICE 'Added column: version to chapters';
    END IF;
END $$;
