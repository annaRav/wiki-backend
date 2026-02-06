-- Create notification_settings table
CREATE TABLE notification_settings
(
    id              UUID PRIMARY KEY,
    user_id         UUID      NOT NULL,
    enable_email    BOOLEAN   NOT NULL,
    enable_push     BOOLEAN   NOT NULL,
    enable_telegram BOOLEAN   NOT NULL,
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP,

    CONSTRAINT uq_notification_settings_user UNIQUE (user_id)
);

-- Create notification_templates table
CREATE TABLE notification_templates
(
    id             UUID PRIMARY KEY,
    type           VARCHAR(50)  NOT NULL,
    title_template VARCHAR(255) NOT NULL,
    body_template  TEXT         NOT NULL,
    created_at     TIMESTAMP    NOT NULL,
    updated_at     TIMESTAMP,

    CONSTRAINT uq_notification_templates_type UNIQUE (type),
    CONSTRAINT chk_notification_templates_type CHECK (type IN ('SMART_GOAL_DEADLINE', 'NEW_MATCH_IN_BACKLOG',
                                                               'PROJECT_STARTED'))
);

-- Create notification_log table
CREATE TABLE notification_log
(
    id         UUID PRIMARY KEY,
    user_id    UUID        NOT NULL,
    content    TEXT        NOT NULL,
    channel    VARCHAR(20) NOT NULL,       -- EMAIL, WS, TG
    status     VARCHAR(20) NOT NULL DEFAULT 'SENT', -- SENT, READ, FAILED
    created_at TIMESTAMP   NOT NULL,
    updated_at TIMESTAMP,

    CONSTRAINT chk_notification_log_channel CHECK (channel IN ('EMAIL', 'WS', 'TG')),
    CONSTRAINT chk_notification_log_status CHECK (status IN ('SENT', 'READ', 'FAILED'))
);
CREATE INDEX idx_notification_log_user_status ON notification_log (user_id, status);
CREATE INDEX idx_notification_log_created_at ON notification_log (created_at);
