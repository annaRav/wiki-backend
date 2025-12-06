-- Organizations table
CREATE TABLE organizations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    logo_url VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_organization_slug ON organizations(slug);
CREATE INDEX idx_organization_active ON organizations(active);

-- Roles table
CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    system_role BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_role_organization FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    CONSTRAINT uk_organization_role_name UNIQUE (organization_id, name)
);

CREATE INDEX idx_role_organization ON roles(organization_id);
CREATE INDEX idx_role_system ON roles(system_role);

-- Permissions table
CREATE TABLE permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100) NOT NULL
);

CREATE INDEX idx_permission_code ON permissions(code);
CREATE INDEX idx_permission_category ON permissions(category);

-- Role-Permission mapping table
CREATE TABLE role_permissions (
    role_id UUID NOT NULL,
    permission_id UUID NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permission_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_role_permission_permission FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

CREATE INDEX idx_role_permission_role ON role_permissions(role_id);
CREATE INDEX idx_role_permission_permission ON role_permissions(permission_id);

-- User-Role mapping table
CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    organization_id UUID NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id, organization_id),
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_role_organization FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_role_user ON user_roles(user_id);
CREATE INDEX idx_user_role_organization ON user_roles(organization_id);
CREATE INDEX idx_user_role_role ON user_roles(role_id);

-- Insert default permissions
INSERT INTO permissions (code, name, description, category) VALUES
    ('org.read', 'Read Organization', 'View organization details', 'organization'),
    ('org.write', 'Write Organization', 'Create and update organizations', 'organization'),
    ('org.delete', 'Delete Organization', 'Delete organizations', 'organization'),
    ('role.read', 'Read Roles', 'View roles', 'role'),
    ('role.write', 'Write Roles', 'Create and update roles', 'role'),
    ('role.delete', 'Delete Roles', 'Delete roles', 'role'),
    ('user.read', 'Read Users', 'View users', 'user'),
    ('user.write', 'Write Users', 'Manage users', 'user'),
    ('user.delete', 'Delete Users', 'Remove users', 'user');
