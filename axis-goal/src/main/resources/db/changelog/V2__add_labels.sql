-- Labels table
CREATE TABLE labels (
    id          UUID         NOT NULL PRIMARY KEY,
    display_name VARCHAR(100) NOT NULL,
    color       VARCHAR(7)   NOT NULL,
    user_id     UUID         NOT NULL
);

-- Join table for Goal <-> Label many-to-many
CREATE TABLE goal_labels (
    goal_id  UUID NOT NULL REFERENCES goals(id) ON DELETE CASCADE,
    label_id UUID NOT NULL REFERENCES labels(id) ON DELETE CASCADE,
    PRIMARY KEY (goal_id, label_id)
);

CREATE INDEX idx_labels_user_id ON labels(user_id);