-- ============================================================================
-- V6: Add plan_type column to profiles table
-- ============================================================================
-- ISSUE: Hibernate schema validation fails because 'plan_type' column is missing
-- FIX: Add column with DEFAULT 'FREE' for existing users (matches PlanTypeEntity enum)
-- ============================================================================

DO $$
BEGIN
    -- Check if column 'plan_type' is missing from 'profiles'
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'profiles'
          AND column_name = 'plan_type'
    ) THEN

        -- Add column with default value for existing records
        ALTER TABLE public.profiles
            ADD COLUMN plan_type VARCHAR(50) NOT NULL DEFAULT 'FREE';

        RAISE NOTICE 'Column plan_type added to profiles table';

    ELSE
        RAISE NOTICE 'Column plan_type already exists in profiles table, skipping';
    END IF;
END $$;
