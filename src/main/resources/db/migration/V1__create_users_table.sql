-- V1__create_users_table.sql
-- Identity Module: Users table
-- IF NOT EXISTS: Prevents crash if table already exists in production

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    plan_type VARCHAR(10) NOT NULL DEFAULT 'FREE',
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT chk_plan_type CHECK (plan_type IN ('FREE', 'PRO'))
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
