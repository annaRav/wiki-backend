CREATE TABLE user_profiles (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id       UUID         NOT NULL UNIQUE,
    display_name  VARCHAR(100),
    bio           TEXT,
    avatar_url    VARCHAR(500),
    timezone      VARCHAR(50),
    date_of_birth DATE,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP
);

CREATE TABLE user_settings (
    id                UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id           UUID        NOT NULL UNIQUE,
    theme             VARCHAR(20) NOT NULL DEFAULT 'SYSTEM',
    locale            VARCHAR(10) NOT NULL DEFAULT 'en_US',
    accent_color      VARCHAR(7),
    two_factor_enabled BOOLEAN    NOT NULL DEFAULT FALSE,
    week_starts_on    VARCHAR(10) NOT NULL DEFAULT 'MONDAY',
    default_goal_view VARCHAR(20) NOT NULL DEFAULT 'LIST',
    created_at        TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP
);

CREATE TABLE user_social_links (
    id                 UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id            UUID         NOT NULL UNIQUE,
    telegram_username  VARCHAR(100),
    telegram_chat_id   BIGINT,
    email              VARCHAR(255),
    updated_at         TIMESTAMP
);

CREATE TABLE user_stats (
    id               UUID    PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id          UUID    NOT NULL UNIQUE,
    total_goals      INT     NOT NULL DEFAULT 0,
    completed_goals  INT     NOT NULL DEFAULT 0,
    active_goals     INT     NOT NULL DEFAULT 0,
    updated_at       TIMESTAMP
);