-- V4: Refactor goal hierarchy — replace GoalType with LifeAspect / Goal / SubGoal

-- 1. Create life_aspects table
CREATE TABLE life_aspects (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    status      VARCHAR(20)  NOT NULL DEFAULT 'NOT_STARTED'
                             CHECK (status IN ('NOT_STARTED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'ON_HOLD')),
    user_id     UUID         NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT now()
);
CREATE INDEX idx_life_aspects_user_id ON life_aspects(user_id);

-- 2. Add life_aspect_id to goals (nullable first, then set NOT NULL after data migration if needed)
ALTER TABLE goals ADD COLUMN life_aspect_id UUID REFERENCES life_aspects(id) ON DELETE CASCADE;
CREATE INDEX idx_goals_life_aspect_id ON goals(life_aspect_id);

-- 3. Drop old goal hierarchy columns from goals
ALTER TABLE goals DROP COLUMN IF EXISTS goal_type_id;
ALTER TABLE goals DROP COLUMN IF EXISTS parent_id;

-- 4. Create sub_goals table
CREATE TABLE sub_goals (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    status      VARCHAR(20)  NOT NULL DEFAULT 'NOT_STARTED'
                             CHECK (status IN ('NOT_STARTED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'ON_HOLD')),
    user_id     UUID         NOT NULL,
    goal_id     UUID         NOT NULL REFERENCES goals(id) ON DELETE CASCADE,
    created_at  TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT now()
);
CREATE INDEX idx_sub_goals_goal_id  ON sub_goals(goal_id);
CREATE INDEX idx_sub_goals_user_id  ON sub_goals(user_id);

-- 5. Create life_aspect_labels join table
CREATE TABLE life_aspect_labels (
    life_aspect_id UUID NOT NULL REFERENCES life_aspects(id) ON DELETE CASCADE,
    label_id       UUID NOT NULL REFERENCES labels(id)       ON DELETE CASCADE,
    PRIMARY KEY (life_aspect_id, label_id)
);

-- 6. Create sub_goal_labels join table
CREATE TABLE sub_goal_labels (
    sub_goal_id UUID NOT NULL REFERENCES sub_goals(id) ON DELETE CASCADE,
    label_id    UUID NOT NULL REFERENCES labels(id)    ON DELETE CASCADE,
    PRIMARY KEY (sub_goal_id, label_id)
);

-- 7. Modify custom_field_definitions: replace goal_type_id with owner_type + user_id
ALTER TABLE custom_field_definitions DROP COLUMN IF EXISTS goal_type_id;
ALTER TABLE custom_field_definitions
    ADD COLUMN owner_type VARCHAR(20) NOT NULL DEFAULT 'GOAL'
               CHECK (owner_type IN ('LIFE_ASPECT', 'GOAL', 'SUB_GOAL'));
ALTER TABLE custom_field_definitions
    ADD COLUMN user_id UUID;
-- Note: user_id is nullable here since we drop old data; enforce NOT NULL in application
DROP INDEX IF EXISTS idx_custom_field_definitions_goal_type_id;
CREATE INDEX idx_custom_field_defs_owner ON custom_field_definitions(owner_type, user_id);

-- 8. Modify custom_field_answers: replace goal_id with owner_id
ALTER TABLE custom_field_answers DROP COLUMN IF EXISTS goal_id;
ALTER TABLE custom_field_answers ADD COLUMN owner_id UUID;
CREATE INDEX idx_custom_field_answers_owner ON custom_field_answers(owner_id);

-- 9. Modify checklists: make goal_id nullable, add sub_goal_id
ALTER TABLE checklists ALTER COLUMN goal_id DROP NOT NULL;
ALTER TABLE checklists
    ADD COLUMN sub_goal_id UUID REFERENCES sub_goals(id) ON DELETE CASCADE;
ALTER TABLE checklists
    ADD CONSTRAINT checklists_single_owner CHECK (
        (goal_id IS NOT NULL)::int + (sub_goal_id IS NOT NULL)::int = 1
    );

-- 10. Drop goal_types table (and its dependent objects)
DROP TABLE IF EXISTS goal_types CASCADE;
