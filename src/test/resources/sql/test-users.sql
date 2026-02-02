-- Test users for integration tests
-- These users are required for FK constraints on projects table

INSERT INTO users (id, email, password_hash, full_name, plan_type, created_at, updated_at, version)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'user1@test.com', '$2a$10$dummyhash1', 'Test User 1', 'FREE', NOW(), NOW(), 0),
    ('22222222-2222-2222-2222-222222222222', 'user2@test.com', '$2a$10$dummyhash2', 'Test User 2', 'FREE', NOW(), NOW(), 0),
    ('33333333-3333-3333-3333-333333333333', 'owner@test.com', '$2a$10$dummyhash3', 'Owner User', 'PRO', NOW(), NOW(), 0),
    ('44444444-4444-4444-4444-444444444444', 'other@test.com', '$2a$10$dummyhash4', 'Other User', 'FREE', NOW(), NOW(), 0)
ON CONFLICT (id) DO NOTHING;
