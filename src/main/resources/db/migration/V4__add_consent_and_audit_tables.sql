-- V4__add_consent_and_audit_tables.sql
-- Add GDPR consent fields to profiles and create audit tables
-- BLINDADO: Maneja tablas existentes sin columnas

-- =============================================================================
-- Step 1: Add consent fields to profiles table
-- =============================================================================
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='profiles' AND column_name='terms_accepted') THEN
        ALTER TABLE profiles ADD COLUMN terms_accepted BOOLEAN NOT NULL DEFAULT false;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='profiles' AND column_name='terms_accepted_at') THEN
        ALTER TABLE profiles ADD COLUMN terms_accepted_at TIMESTAMPTZ;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='profiles' AND column_name='privacy_accepted') THEN
        ALTER TABLE profiles ADD COLUMN privacy_accepted BOOLEAN NOT NULL DEFAULT false;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='profiles' AND column_name='privacy_accepted_at') THEN
        ALTER TABLE profiles ADD COLUMN privacy_accepted_at TIMESTAMPTZ;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='profiles' AND column_name='marketing_accepted') THEN
        ALTER TABLE profiles ADD COLUMN marketing_accepted BOOLEAN NOT NULL DEFAULT false;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='profiles' AND column_name='marketing_accepted_at') THEN
        ALTER TABLE profiles ADD COLUMN marketing_accepted_at TIMESTAMPTZ;
    END IF;
END $$;

-- =============================================================================
-- Step 2: Create consent_logs table
-- =============================================================================
CREATE TABLE IF NOT EXISTS consent_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    consent_type VARCHAR(50) NOT NULL,
    consent_given BOOLEAN NOT NULL,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    recorded_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Inyectar columnas si faltan
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='consent_logs' AND column_name='consent_type') THEN
        ALTER TABLE consent_logs ADD COLUMN consent_type VARCHAR(50) NOT NULL DEFAULT 'UNKNOWN';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='consent_logs' AND column_name='consent_given') THEN
        ALTER TABLE consent_logs ADD COLUMN consent_given BOOLEAN NOT NULL DEFAULT false;
    END IF;
END $$;

-- FK para consent_logs (si no existe)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_consent_logs_user') THEN
        ALTER TABLE consent_logs ADD CONSTRAINT fk_consent_logs_user
            FOREIGN KEY (user_id) REFERENCES profiles(id) ON DELETE CASCADE;
    END IF;
END $$;

-- Índices consent_logs
CREATE INDEX IF NOT EXISTS idx_consent_logs_user_id ON consent_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_consent_logs_consent_type ON consent_logs(consent_type);
CREATE INDEX IF NOT EXISTS idx_consent_logs_recorded_at ON consent_logs(recorded_at DESC);

-- =============================================================================
-- Step 3: Create app_audit_logs table
-- =============================================================================
CREATE TABLE IF NOT EXISTS app_audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID,
    action_type VARCHAR(100) NOT NULL,
    performed_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Inyectar columnas si faltan (CRÍTICO para el índice de entity_id)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='app_audit_logs' AND column_name='entity_id') THEN
        ALTER TABLE app_audit_logs ADD COLUMN entity_id VARCHAR(100);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='app_audit_logs' AND column_name='details') THEN
        ALTER TABLE app_audit_logs ADD COLUMN details JSONB DEFAULT '{}';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='app_audit_logs' AND column_name='ip_address') THEN
        ALTER TABLE app_audit_logs ADD COLUMN ip_address VARCHAR(45);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='app_audit_logs' AND column_name='user_agent') THEN
        ALTER TABLE app_audit_logs ADD COLUMN user_agent VARCHAR(500);
    END IF;
END $$;

-- FK para app_audit_logs (si no existe)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_audit_logs_user') THEN
        ALTER TABLE app_audit_logs ADD CONSTRAINT fk_audit_logs_user
            FOREIGN KEY (user_id) REFERENCES profiles(id) ON DELETE SET NULL;
    END IF;
END $$;

-- Índices app_audit_logs (AHORA es seguro porque entity_id existe)
CREATE INDEX IF NOT EXISTS idx_audit_logs_user_id ON app_audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_action_type ON app_audit_logs(action_type);
CREATE INDEX IF NOT EXISTS idx_audit_logs_entity_id ON app_audit_logs(entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_performed_at ON app_audit_logs(performed_at DESC);
CREATE INDEX IF NOT EXISTS idx_audit_logs_details ON app_audit_logs USING GIN (details);
