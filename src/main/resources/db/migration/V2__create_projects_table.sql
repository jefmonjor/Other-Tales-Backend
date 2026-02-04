-- V2__create_projects_table.sql
-- Writing Module: Projects table
-- NO FK aquí - se crea en V3 después del renombrado users→profiles

CREATE TABLE IF NOT EXISTS projects (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,  -- FK diferida a V3
    title VARCHAR(255) NOT NULL,
    synopsis VARCHAR(2000),
    genre VARCHAR(100),
    current_word_count INTEGER NOT NULL DEFAULT 0,
    target_word_count INTEGER NOT NULL DEFAULT 50000,
    cover_url VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT chk_project_status CHECK (status IN ('DRAFT', 'PUBLISHED')),
    CONSTRAINT chk_current_word_count CHECK (current_word_count >= 0),
    CONSTRAINT chk_target_word_count CHECK (target_word_count >= 0)
);

-- Índices (sin FK - se añade en V3)
CREATE INDEX IF NOT EXISTS idx_projects_user_id ON projects(user_id);
CREATE INDEX IF NOT EXISTS idx_projects_user_deleted ON projects(user_id, deleted);
CREATE INDEX IF NOT EXISTS idx_projects_status ON projects(status);
