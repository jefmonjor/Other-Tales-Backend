-- V5__fix_missing_audit_columns.sql
-- HOTFIX: Añadir columnas que faltaron en V4 anterior
-- Estas columnas son requeridas por las entidades JPA

-- =============================================================================
-- 1. APP_AUDIT_LOGS: Columnas faltantes
-- =============================================================================
ALTER TABLE app_audit_logs ADD COLUMN IF NOT EXISTS entity_id VARCHAR(100);
ALTER TABLE app_audit_logs ADD COLUMN IF NOT EXISTS details JSONB DEFAULT '{}'::jsonb;
ALTER TABLE app_audit_logs ADD COLUMN IF NOT EXISTS ip_address VARCHAR(45);
ALTER TABLE app_audit_logs ADD COLUMN IF NOT EXISTS user_agent VARCHAR(500);

-- =============================================================================
-- 2. CONSENT_LOGS: Columnas faltantes
-- =============================================================================
ALTER TABLE consent_logs ADD COLUMN IF NOT EXISTS ip_address VARCHAR(45);
ALTER TABLE consent_logs ADD COLUMN IF NOT EXISTS user_agent VARCHAR(500);

-- Asegurar que consent_given existe (por si la tabla tenía 'accepted')
ALTER TABLE consent_logs ADD COLUMN IF NOT EXISTS consent_given BOOLEAN DEFAULT FALSE;
