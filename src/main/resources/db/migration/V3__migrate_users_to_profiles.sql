-- V3__migrate_users_to_profiles.sql
-- Migration to Supabase: Convert users table to profiles table
-- Authentication is now handled by Supabase Auth (auth.users)
-- Made idempotent: safe to run multiple times

-- Step 1: Drop the foreign key constraint on projects (if exists)
ALTER TABLE projects DROP CONSTRAINT IF EXISTS fk_projects_user;

-- Step 2: Handle users → profiles migration (idempotent, handles all cases)
DO $$
BEGIN
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
END $$;

-- Step 3: Drop the password_hash column (authentication handled by Supabase)
ALTER TABLE profiles DROP COLUMN IF EXISTS password_hash;

-- Step 4: Rename index
DROP INDEX IF EXISTS idx_users_email;
CREATE INDEX IF NOT EXISTS idx_profiles_email ON profiles(email);

-- Step 5: Recreate the foreign key constraint on projects referencing profiles (idempotent)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_projects_profile'
        AND table_name = 'projects'
    ) THEN
        ALTER TABLE projects ADD CONSTRAINT fk_projects_profile
            FOREIGN KEY (user_id) REFERENCES profiles(id);
    END IF;
END $$;
