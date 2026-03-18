-- V5: Replace GoalStatus with ProgressStatus on goals/sub_goals;
--     replace status with rated_status (1-10) on life_aspects

-- 1. Update goals.status: drop old constraint, add new one with ProgressStatus values
ALTER TABLE goals DROP CONSTRAINT IF EXISTS goals_status_check;
ALTER TABLE goals ALTER COLUMN status TYPE VARCHAR(20);
ALTER TABLE goals ALTER COLUMN status SET DEFAULT 'NOT_REFINE';
ALTER TABLE goals ADD CONSTRAINT goals_status_check
    CHECK (status IN ('NOT_REFINE', 'READY', 'IN_PROGRESS', 'COMPLETED', 'CANCELED'));

-- 2. Update sub_goals.status: same
ALTER TABLE sub_goals DROP CONSTRAINT IF EXISTS sub_goals_status_check;
ALTER TABLE sub_goals ALTER COLUMN status TYPE VARCHAR(20);
ALTER TABLE sub_goals ALTER COLUMN status SET DEFAULT 'NOT_REFINE';
ALTER TABLE sub_goals ADD CONSTRAINT sub_goals_status_check
    CHECK (status IN ('NOT_REFINE', 'READY', 'IN_PROGRESS', 'COMPLETED', 'CANCELED'));

-- 3. Replace life_aspects.status with rated_status (integer 1-10)
ALTER TABLE life_aspects DROP COLUMN IF EXISTS status;
ALTER TABLE life_aspects ADD COLUMN rated_status INTEGER
    CHECK (rated_status >= 1 AND rated_status <= 10);
