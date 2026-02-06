-- Create goal_types table
CREATE TABLE goal_types (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    level_number INTEGER NOT NULL,
    user_id UUID NOT NULL,
    CONSTRAINT uq_goal_types_user_level UNIQUE (user_id, level_number)
);

-- Create index on user_id for faster queries by user
CREATE INDEX idx_goal_types_user_id ON goal_types(user_id);

-- Create custom_field_definitions table
CREATE TABLE custom_field_definitions (
    id UUID PRIMARY KEY,
    label VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    required BOOLEAN NOT NULL,
    placeholder VARCHAR(255),
    goal_type_id UUID NOT NULL,
    CONSTRAINT fk_custom_field_definitions_goal_type FOREIGN KEY (goal_type_id) REFERENCES goal_types(id) ON DELETE CASCADE,
    CONSTRAINT chk_custom_field_type CHECK (type IN ('STRING', 'NUMBER', 'BOOLEAN', 'DATE'))
);

-- Create index on goal_type_id for faster queries
CREATE INDEX idx_custom_field_definitions_goal_type_id ON custom_field_definitions(goal_type_id);

-- Create goals table
CREATE TABLE goals (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    goal_type_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    parent_id UUID,
    user_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_goals_goal_type FOREIGN KEY (goal_type_id) REFERENCES goal_types(id),
    CONSTRAINT fk_goals_parent FOREIGN KEY (parent_id) REFERENCES goals(id) ON DELETE CASCADE,
    CONSTRAINT chk_goal_status CHECK (status IN ('NOT_STARTED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'ON_HOLD'))
);

-- Create indexes on goals table
CREATE INDEX idx_goals_user_id ON goals(user_id);
CREATE INDEX idx_goals_status ON goals(status);
CREATE INDEX idx_goals_goal_type_id ON goals(goal_type_id);
CREATE INDEX idx_goals_parent_id ON goals(parent_id);
CREATE INDEX idx_goals_created_at ON goals(created_at);

-- Create custom_field_answers table
CREATE TABLE custom_field_answers (
    id UUID PRIMARY KEY,
    field_definition_id UUID NOT NULL,
    field_value TEXT,
    goal_id UUID NOT NULL,
    CONSTRAINT fk_custom_field_answers_field_definition FOREIGN KEY (field_definition_id) REFERENCES custom_field_definitions(id) ON DELETE CASCADE,
    CONSTRAINT fk_custom_field_answers_goal FOREIGN KEY (goal_id) REFERENCES goals(id) ON DELETE CASCADE
);

-- Create indexes on custom_field_answers table
CREATE INDEX idx_custom_field_answers_field_definition_id ON custom_field_answers(field_definition_id);
CREATE INDEX idx_custom_field_answers_goal_id ON custom_field_answers(goal_id);