# Database Schemas

## Current Schema: PostgreSQL (wiki-membership service)

### organizations
```sql
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
```

### users
```sql
-- Caches user profiles from Keycloak for quick access
CREATE TABLE users (
    id UUID PRIMARY KEY,  -- Keycloak 'sub' claim
    email VARCHAR(255) NOT NULL UNIQUE,
    preferred_username VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    keycloak_sync_time TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_email ON users(email);
```

**Note:** Primary authentication is handled by Keycloak. This table caches user data for performance and relationship mapping.

### roles
```sql
CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(100) NOT NULL,
    description TEXT,
    system_role BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_role_organization FOREIGN KEY (organization_id)
        REFERENCES organizations(id) ON DELETE CASCADE,
    CONSTRAINT uk_organization_role_name UNIQUE (organization_id, name),
    CONSTRAINT uk_organization_role_code UNIQUE (organization_id, code)
);

CREATE INDEX idx_role_organization ON roles(organization_id);
CREATE INDEX idx_role_system ON roles(system_role);
```

### permissions
```sql
CREATE TABLE permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    category VARCHAR(100) NOT NULL
);

CREATE INDEX idx_permission_code ON permissions(code);
CREATE INDEX idx_permission_category ON permissions(category);

-- Default permissions
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
```

### role_permissions
```sql
CREATE TABLE role_permissions (
    role_id UUID NOT NULL,
    permission_id UUID NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permission_role FOREIGN KEY (role_id)
        REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_role_permission_permission FOREIGN KEY (permission_id)
        REFERENCES permissions(id) ON DELETE CASCADE
);

CREATE INDEX idx_role_permission_role ON role_permissions(role_id);
CREATE INDEX idx_role_permission_permission ON role_permissions(permission_id);
```

### user_roles
```sql
CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    organization_id UUID NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id, organization_id),
    CONSTRAINT fk_user_role_user_id FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id)
        REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_role_organization FOREIGN KEY (organization_id)
        REFERENCES organizations(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_role_user ON user_roles(user_id);
CREATE INDEX idx_user_role_organization ON user_roles(organization_id);
CREATE INDEX idx_user_role_role ON user_roles(role_id);
```

### System Data

**System Organization:**
```sql
-- Global organization for system-level roles
INSERT INTO organizations (id, name, slug, description, active) VALUES
    ('00000000-0000-0000-0000-000000000000',
     'System Global',
     'system-global',
     'Organization for system-level roles and permissions',
     true);
```

**System Roles:**
```sql
-- Superadmin role with full system access
INSERT INTO roles (id, organization_id, name, code, description, system_role) VALUES
    ('00000000-0000-0000-0000-000000000001',
     '00000000-0000-0000-0000-000000000000',
     'Superadmin',
     'SUPERADMIN',
     'Global administrator with full system access',
     true);
```

---

## Planned Schemas: MongoDB (wiki-content service)

### pages collection
```javascript
{
  _id: ObjectId,
  slug: String,           // URL-friendly identifier
  title: String,
  content: String,        // Markdown content
  author_id: UUID,        // Reference to Keycloak user
  organization_id: UUID,  // Reference to PostgreSQL organization
  category_ids: [ObjectId],
  tags: [String],
  is_published: Boolean,
  view_count: Number,
  created_at: ISODate,
  updated_at: ISODate
}

// Indexes
db.pages.createIndex({ slug: 1 }, { unique: true })
db.pages.createIndex({ organization_id: 1 })
db.pages.createIndex({ title: "text", content: "text" })
db.pages.createIndex({ tags: 1 })
```

### revisions collection
```javascript
{
  _id: ObjectId,
  page_id: ObjectId,
  version: Number,
  content: String,
  author_id: UUID,
  comment: String,        // Revision comment
  created_at: ISODate
}

// Index
db.revisions.createIndex({ page_id: 1, version: -1 })
```

### categories collection
```javascript
{
  _id: ObjectId,
  name: String,
  slug: String,
  organization_id: UUID,
  parent_id: ObjectId,    // For nested categories
  description: String
}
```

---

## Planned Schemas: MongoDB (wiki-media service)

### media collection
```javascript
{
  _id: ObjectId,
  filename: String,
  original_name: String,
  mime_type: String,
  size: Number,           // bytes
  storage_path: String,   // S3 key
  thumbnail_path: String,
  uploader_id: UUID,
  organization_id: UUID,
  metadata: {
    width: Number,        // for images
    height: Number,
    duration: Number      // for video/audio
  },
  created_at: ISODate
}
```

---

## Key Conventions

- **UUID Primary Keys:** All PostgreSQL entities use UUID as primary key
- **Timestamps:** All tables include `created_at` and `updated_at` (automatically managed by Hibernate annotations)
- **Soft Deletes:** Use `active` boolean flag for organizations, not actual deletion
- **Foreign Keys:** Always include proper FK constraints with `ON DELETE CASCADE` where appropriate
- **Indexes:** Create indexes on frequently queried columns (slugs, foreign keys, search fields)
- **Naming:** Use snake_case for SQL, camelCase for MongoDB