-- V4__add_consent_and_audit_tables.sql
-- GDPR consent fields + audit tables
-- BLINDADO: Compatible con tablas existentes en Supabase

-- 1. TABLA PERFILES: Asegurar columnas de consentimiento
ALTER TABLE profiles ADD COLUMN IF NOT EXISTS terms_accepted BOOLEAN DEFAULT FALSE;
ALTER TABLE profiles ADD COLUMN IF NOT EXISTS privacy_accepted BOOLEAN DEFAULT FALSE;
ALTER TABLE profiles ADD COLUMN IF NOT EXISTS marketing_accepted BOOLEAN DEFAULT FALSE;
ALTER TABLE profiles ADD COLUMN IF NOT EXISTS terms_accepted_at TIMESTAMPTZ;
ALTER TABLE profiles ADD COLUMN IF NOT EXISTS privacy_accepted_at TIMESTAMPTZ;
ALTER TABLE profiles ADD COLUMN IF NOT EXISTS marketing_accepted_at TIMESTAMPTZ;

-- 2. TABLA CONSENT_LOGS (Si no existe)
CREATE TABLE IF NOT EXISTS consent_logs (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID NOT NULL,
    consent_type VARCHAR(50) NOT NULL,
    consent_version VARCHAR(20) NOT NULL DEFAULT '1.0',
    accepted BOOLEAN NOT NULL,
    ip_address VARCHAR(45),
    user_agent TEXT,
    recorded_at TIMESTAMPTZ DEFAULT NOW()
);

-- FK para consent_logs (defensiva)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_consent_logs_profile') THEN
        ALTER TABLE consent_logs ADD CONSTRAINT fk_consent_logs_profile
            FOREIGN KEY (user_id) REFERENCES profiles(id) ON DELETE CASCADE;
    END IF;
END $$;

-- 3. TABLA APP_AUDIT_LOGS (Defensiva)
CREATE TABLE IF NOT EXISTS app_audit_logs (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID,
    action_type VARCHAR(50) NOT NULL,
    details JSONB DEFAULT '{}'::jsonb,
    performed_at TIMESTAMPTZ DEFAULT NOW()
);

-- FK para app_audit_logs (defensiva)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_audit_logs_profile') THEN
        ALTER TABLE app_audit_logs ADD CONSTRAINT fk_audit_logs_profile
            FOREIGN KEY (user_id) REFERENCES profiles(id) ON DELETE SET NULL;
    END IF;
END $$;

-- FIX CRÍTICO: Inyectar 'entity_id' si falta
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name='app_audit_logs' AND column_name='entity_id') THEN
        ALTER TABLE app_audit_logs ADD COLUMN entity_id VARCHAR(255);
    END IF;
END $$;

-- 4. ÍNDICES (Ahora seguros)
CREATE INDEX IF NOT EXISTS idx_consent_user_id ON consent_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_user_id ON app_audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_action_type ON app_audit_logs(action_type);
CREATE INDEX IF NOT EXISTS idx_audit_entity_id ON app_audit_logs(entity_id);
