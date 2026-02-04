-- V4__add_consent_and_audit_tables.sql
-- GDPR consent fields + audit tables
-- BLINDADO: Compatible con entidades JPA y tablas Supabase existentes

-- =============================================================================
-- 1. TABLA PERFILES: Columnas de consentimiento
-- =============================================================================
ALTER TABLE profiles ADD COLUMN IF NOT EXISTS terms_accepted BOOLEAN DEFAULT FALSE;
ALTER TABLE profiles ADD COLUMN IF NOT EXISTS privacy_accepted BOOLEAN DEFAULT FALSE;
ALTER TABLE profiles ADD COLUMN IF NOT EXISTS marketing_accepted BOOLEAN DEFAULT FALSE;
ALTER TABLE profiles ADD COLUMN IF NOT EXISTS terms_accepted_at TIMESTAMPTZ;
ALTER TABLE profiles ADD COLUMN IF NOT EXISTS privacy_accepted_at TIMESTAMPTZ;
ALTER TABLE profiles ADD COLUMN IF NOT EXISTS marketing_accepted_at TIMESTAMPTZ;

-- =============================================================================
-- 2. TABLA CONSENT_LOGS
-- =============================================================================
CREATE TABLE IF NOT EXISTS consent_logs (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID NOT NULL,
    consent_type VARCHAR(50) NOT NULL,
    consent_given BOOLEAN NOT NULL DEFAULT FALSE,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    recorded_at TIMESTAMPTZ DEFAULT NOW()
);

-- Inyectar columnas si faltan (por si tabla existe con estructura diferente)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='consent_logs' AND column_name='consent_given') THEN
        ALTER TABLE consent_logs ADD COLUMN consent_given BOOLEAN NOT NULL DEFAULT FALSE;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='consent_logs' AND column_name='ip_address') THEN
        ALTER TABLE consent_logs ADD COLUMN ip_address VARCHAR(45);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='consent_logs' AND column_name='user_agent') THEN
        ALTER TABLE consent_logs ADD COLUMN user_agent VARCHAR(500);
    END IF;
END $$;

-- FK para consent_logs
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_consent_logs_profile') THEN
        ALTER TABLE consent_logs ADD CONSTRAINT fk_consent_logs_profile
            FOREIGN KEY (user_id) REFERENCES profiles(id) ON DELETE CASCADE;
    END IF;
END $$;

-- =============================================================================
-- 3. TABLA APP_AUDIT_LOGS
-- =============================================================================
CREATE TABLE IF NOT EXISTS app_audit_logs (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID,
    action_type VARCHAR(100) NOT NULL,
    entity_id VARCHAR(100),
    details JSONB DEFAULT '{}'::jsonb,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    performed_at TIMESTAMPTZ DEFAULT NOW()
);

-- CRÍTICO: Inyectar TODAS las columnas que la entidad JPA espera
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='app_audit_logs' AND column_name='entity_id') THEN
        ALTER TABLE app_audit_logs ADD COLUMN entity_id VARCHAR(100);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='app_audit_logs' AND column_name='details') THEN
        ALTER TABLE app_audit_logs ADD COLUMN details JSONB DEFAULT '{}'::jsonb;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='app_audit_logs' AND column_name='ip_address') THEN
        ALTER TABLE app_audit_logs ADD COLUMN ip_address VARCHAR(45);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='app_audit_logs' AND column_name='user_agent') THEN
        ALTER TABLE app_audit_logs ADD COLUMN user_agent VARCHAR(500);
    END IF;
END $$;

-- FK para app_audit_logs
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_audit_logs_profile') THEN
        ALTER TABLE app_audit_logs ADD CONSTRAINT fk_audit_logs_profile
            FOREIGN KEY (user_id) REFERENCES profiles(id) ON DELETE SET NULL;
    END IF;
END $$;

-- =============================================================================
-- 4. ÍNDICES
-- =============================================================================
CREATE INDEX IF NOT EXISTS idx_consent_logs_user_id ON consent_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_consent_logs_consent_type ON consent_logs(consent_type);
CREATE INDEX IF NOT EXISTS idx_consent_logs_recorded_at ON consent_logs(recorded_at);

CREATE INDEX IF NOT EXISTS idx_audit_logs_user_id ON app_audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_action_type ON app_audit_logs(action_type);
CREATE INDEX IF NOT EXISTS idx_audit_logs_entity_id ON app_audit_logs(entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_performed_at ON app_audit_logs(performed_at);
