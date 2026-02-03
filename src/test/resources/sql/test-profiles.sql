-- Test profiles for integration tests
-- These profiles are required for FK constraints on projects table
-- Note: In the new architecture, authentication is handled by Supabase.
-- These are local test profiles for Testcontainers.

INSERT INTO profiles (id, email, full_name, plan_type, created_at, updated_at, version)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'user1@test.com', 'Test User 1', 'FREE', NOW(), NOW(), 0),
    ('22222222-2222-2222-2222-222222222222', 'user2@test.com', 'Test User 2', 'FREE', NOW(), NOW(), 0),
    ('33333333-3333-3333-3333-333333333333', 'owner@test.com', 'Owner User', 'PRO', NOW(), NOW(), 0),
    ('44444444-4444-4444-4444-444444444444', 'other@test.com', 'Other User', 'FREE', NOW(), NOW(), 0)
ON CONFLICT (id) DO NOTHING;
