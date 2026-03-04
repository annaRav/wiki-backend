-- Checklists belong to a goal and hold ordered lists of items
CREATE TABLE checklists (
    id         UUID         NOT NULL PRIMARY KEY,
    title      VARCHAR(255) NOT NULL,
    position   INTEGER      NOT NULL DEFAULT 0,
    goal_id    UUID         NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_checklists_goal FOREIGN KEY (goal_id) REFERENCES goals(id) ON DELETE CASCADE
);

CREATE INDEX idx_checklists_goal_id ON checklists(goal_id);

-- Checklist items represent individual actionable tasks within a checklist
CREATE TABLE checklist_items (
    id           UUID         NOT NULL PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    is_completed BOOLEAN      NOT NULL DEFAULT FALSE,
    position     INTEGER      NOT NULL DEFAULT 0,
    checklist_id UUID         NOT NULL,
    created_at   TIMESTAMP    NOT NULL,
    updated_at   TIMESTAMP,
    CONSTRAINT fk_checklist_items_checklist FOREIGN KEY (checklist_id) REFERENCES checklists(id) ON DELETE CASCADE
);

CREATE INDEX idx_checklist_items_checklist_id ON checklist_items(checklist_id);
