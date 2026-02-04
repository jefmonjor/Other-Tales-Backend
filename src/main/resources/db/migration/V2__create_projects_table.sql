-- V2__create_projects_table.sql
-- Writing Module: Projects table
-- IF NOT EXISTS: Prevents crash if table already exists in production

CREATE TABLE IF NOT EXISTS projects (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    synopsis VARCHAR(2000),
    genre VARCHAR(100),
    current_word_count INTEGER NOT NULL DEFAULT 0,
    target_word_count INTEGER NOT NULL DEFAULT 50000,
    cover_url VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT chk_project_status CHECK (status IN ('DRAFT', 'PUBLISHED')),
    CONSTRAINT chk_current_word_count CHECK (current_word_count >= 0),
    CONSTRAINT chk_target_word_count CHECK (target_word_count >= 0)
);

-- Add foreign key only if it doesn't exist (idempotent)
-- IMPORTANT: References profiles(id), NOT users(id) - Supabase schema
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_projects_user'
        AND table_name = 'projects'
    ) THEN
        ALTER TABLE projects ADD CONSTRAINT fk_projects_user
            FOREIGN KEY (user_id) REFERENCES profiles(id);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_projects_user_id ON projects(user_id);
CREATE INDEX IF NOT EXISTS idx_projects_user_deleted ON projects(user_id, deleted);
CREATE INDEX IF NOT EXISTS idx_projects_status ON projects(status);
