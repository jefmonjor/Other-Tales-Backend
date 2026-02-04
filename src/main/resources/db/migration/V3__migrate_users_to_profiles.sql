-- V3__migrate_users_to_profiles.sql
-- Migration to Supabase: Convert users table to profiles table
-- Authentication is now handled by Supabase Auth (auth.users)
-- Made idempotent: safe to run multiple times

-- Step 1: Drop the foreign key constraint on projects (if exists)
ALTER TABLE projects DROP CONSTRAINT IF EXISTS fk_projects_user;

-- Step 2: Rename users table to profiles (only if users exists and profiles doesn't)
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'users' AND table_schema = 'public')
       AND NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'profiles' AND table_schema = 'public')
    THEN
        ALTER TABLE users RENAME TO profiles;
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
