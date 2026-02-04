-- V4__add_consent_and_audit_tables.sql
-- Add GDPR consent fields to profiles and create audit tables

-- =============================================================================
-- Step 1: Add consent fields to profiles table
-- =============================================================================
ALTER TABLE profiles
    ADD COLUMN IF NOT EXISTS terms_accepted BOOLEAN NOT NULL DEFAULT false,
    ADD COLUMN IF NOT EXISTS terms_accepted_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS privacy_accepted BOOLEAN NOT NULL DEFAULT false,
    ADD COLUMN IF NOT EXISTS privacy_accepted_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS marketing_accepted BOOLEAN NOT NULL DEFAULT false,
    ADD COLUMN IF NOT EXISTS marketing_accepted_at TIMESTAMPTZ;

-- =============================================================================
-- Step 2: Create consent_logs table for GDPR audit trail
-- =============================================================================
CREATE TABLE IF NOT EXISTS consent_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    consent_type VARCHAR(50) NOT NULL,
    consent_given BOOLEAN NOT NULL,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    recorded_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_consent_type CHECK (consent_type IN ('TERMS_OF_SERVICE', 'PRIVACY_POLICY', 'MARKETING_COMMUNICATIONS'))
);

-- Indexes for efficient querying
CREATE INDEX IF NOT EXISTS idx_consent_logs_user_id ON consent_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_consent_logs_consent_type ON consent_logs(consent_type);
CREATE INDEX IF NOT EXISTS idx_consent_logs_recorded_at ON consent_logs(recorded_at DESC);

-- =============================================================================
-- Step 3: Create app_audit_logs table for general audit trail
-- =============================================================================
CREATE TABLE IF NOT EXISTS app_audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES profiles(id) ON DELETE SET NULL,
    action_type VARCHAR(100) NOT NULL,
    entity_id VARCHAR(100),
    details JSONB DEFAULT '{}',
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    performed_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes for efficient querying
CREATE INDEX IF NOT EXISTS idx_audit_logs_user_id ON app_audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_action_type ON app_audit_logs(action_type);
CREATE INDEX IF NOT EXISTS idx_audit_logs_entity_id ON app_audit_logs(entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_performed_at ON app_audit_logs(performed_at DESC);

-- GIN index for JSONB queries
CREATE INDEX IF NOT EXISTS idx_audit_logs_details ON app_audit_logs USING GIN (details);

-- =============================================================================
-- Step 4: Add comments for documentation
-- =============================================================================
COMMENT ON TABLE consent_logs IS 'Immutable audit log for GDPR consent changes';
COMMENT ON COLUMN consent_logs.consent_type IS 'Type of consent: TERMS_OF_SERVICE, PRIVACY_POLICY, MARKETING_COMMUNICATIONS';
COMMENT ON COLUMN consent_logs.consent_given IS 'True if consent was granted, false if revoked';

COMMENT ON TABLE app_audit_logs IS 'General audit log for tracking all system events';
COMMENT ON COLUMN app_audit_logs.action_type IS 'Event type following pattern ENTITY.ACTION (e.g., PROJECT.CREATED)';
COMMENT ON COLUMN app_audit_logs.details IS 'JSON object with additional event details';
