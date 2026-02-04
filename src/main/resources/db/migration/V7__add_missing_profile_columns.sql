-- ============================================================================
-- V7: Add all missing columns to profiles table
-- ============================================================================
-- ISSUE: Hibernate schema validation fails due to multiple missing columns
-- FIX: Add consent fields, timestamps, and version column in one migration
-- ============================================================================

DO $$
BEGIN
    -- ========== CONSENT FIELDS (GDPR Compliance) ==========

    -- terms_accepted
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_schema='public' AND table_name='profiles' AND column_name='terms_accepted') THEN
        ALTER TABLE public.profiles ADD COLUMN terms_accepted BOOLEAN NOT NULL DEFAULT false;
        RAISE NOTICE 'Added column: terms_accepted';
    END IF;

    -- terms_accepted_at
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_schema='public' AND table_name='profiles' AND column_name='terms_accepted_at') THEN
        ALTER TABLE public.profiles ADD COLUMN terms_accepted_at TIMESTAMPTZ;
        RAISE NOTICE 'Added column: terms_accepted_at';
    END IF;

    -- privacy_accepted
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_schema='public' AND table_name='profiles' AND column_name='privacy_accepted') THEN
        ALTER TABLE public.profiles ADD COLUMN privacy_accepted BOOLEAN NOT NULL DEFAULT false;
        RAISE NOTICE 'Added column: privacy_accepted';
    END IF;

    -- privacy_accepted_at
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_schema='public' AND table_name='profiles' AND column_name='privacy_accepted_at') THEN
        ALTER TABLE public.profiles ADD COLUMN privacy_accepted_at TIMESTAMPTZ;
        RAISE NOTICE 'Added column: privacy_accepted_at';
    END IF;

    -- marketing_accepted
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_schema='public' AND table_name='profiles' AND column_name='marketing_accepted') THEN
        ALTER TABLE public.profiles ADD COLUMN marketing_accepted BOOLEAN NOT NULL DEFAULT false;
        RAISE NOTICE 'Added column: marketing_accepted';
    END IF;

    -- marketing_accepted_at
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_schema='public' AND table_name='profiles' AND column_name='marketing_accepted_at') THEN
        ALTER TABLE public.profiles ADD COLUMN marketing_accepted_at TIMESTAMPTZ;
        RAISE NOTICE 'Added column: marketing_accepted_at';
    END IF;

    -- ========== TIMESTAMPS ==========

    -- created_at
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_schema='public' AND table_name='profiles' AND column_name='created_at') THEN
        ALTER TABLE public.profiles ADD COLUMN created_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
        RAISE NOTICE 'Added column: created_at';
    END IF;

    -- updated_at
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_schema='public' AND table_name='profiles' AND column_name='updated_at') THEN
        ALTER TABLE public.profiles ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
        RAISE NOTICE 'Added column: updated_at';
    END IF;

    -- ========== JPA VERSION (Optimistic Locking) ==========

    -- version
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_schema='public' AND table_name='profiles' AND column_name='version') THEN
        ALTER TABLE public.profiles ADD COLUMN version BIGINT DEFAULT 0;
        RAISE NOTICE 'Added column: version';
    END IF;

END $$;
