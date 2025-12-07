-- New table for caching user profiles from Keycloak
CREATE TABLE users (
    id UUID PRIMARY KEY, -- Keycloak 'sub'
    email VARCHAR(255) NOT NULL UNIQUE,
    preferred_username VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    keycloak_sync_time TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_email ON users(email);

-- Update permissions table: add unique constraint on 'name'
ALTER TABLE permissions ADD CONSTRAINT uk_permission_name UNIQUE (name);

-- Update roles table: add 'code' column and unique constraint
ALTER TABLE roles ADD COLUMN code VARCHAR(100) NOT NULL DEFAULT 'TEMP';
ALTER TABLE roles ADD CONSTRAINT uk_organization_role_code UNIQUE (organization_id, code);

-- Drop temporary default, as 'code' should be set upon creation
ALTER TABLE roles ALTER COLUMN code DROP DEFAULT;

-- Update user_roles table to add FK constraint to the new users table (for data integrity)
ALTER TABLE user_roles ADD CONSTRAINT fk_user_role_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- Insert an example user (SUPERADMIN) who will register the organization
INSERT INTO users (id, email, preferred_username, first_name, last_name, keycloak_sync_time) VALUES
    ('00000000-0000-0000-0000-000000000001', 'superadmin@wiki.com', 'superadmin', 'System', 'Admin', CURRENT_TIMESTAMP);

-- Create system organization for global roles
INSERT INTO organizations (id, name, slug, description, active) VALUES
    ('00000000-0000-0000-0000-000000000000', 'System Global', 'system-global', 'Organization for system-level roles and permissions', true);

-- Insert Superadmin role in system organization
INSERT INTO roles (id, organization_id, name, code, description, system_role) VALUES
    ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000000', 'Superadmin', 'SUPERADMIN', 'Global administrator with full system access', true);

-- Assign Superadmin role to first user
INSERT INTO user_roles (user_id, role_id, organization_id) VALUES
    ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000000');
