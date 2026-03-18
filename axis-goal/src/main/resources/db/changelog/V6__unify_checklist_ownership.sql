-- V6: Unify checklist ownership — replace goal_id/sub_goal_id with owner_id/owner_type

-- 1. Drop old constraint and index
ALTER TABLE checklists DROP CONSTRAINT IF EXISTS checklists_single_owner;
DROP INDEX IF EXISTS idx_checklists_goal_id;

-- 2. Add owner_id and owner_type
ALTER TABLE checklists ADD COLUMN owner_id   UUID        NULL;
ALTER TABLE checklists ADD COLUMN owner_type VARCHAR(20) NULL;

-- 3. Migrate existing data
UPDATE checklists SET owner_id = goal_id,    owner_type = 'GOAL'     WHERE goal_id    IS NOT NULL;
UPDATE checklists SET owner_id = sub_goal_id, owner_type = 'SUB_GOAL' WHERE sub_goal_id IS NOT NULL;

-- 4. Enforce NOT NULL and check constraint
ALTER TABLE checklists ALTER COLUMN owner_id   SET NOT NULL;
ALTER TABLE checklists ALTER COLUMN owner_type SET NOT NULL;
ALTER TABLE checklists ADD CONSTRAINT chk_checklist_owner_type
    CHECK (owner_type IN ('GOAL', 'SUB_GOAL'));

-- 5. Drop old FK columns
ALTER TABLE checklists DROP COLUMN goal_id;
ALTER TABLE checklists DROP COLUMN sub_goal_id;

-- 6. Add index
CREATE INDEX idx_checklists_owner_id ON checklists(owner_id);
