CREATE TABLE sensor_logs
(
    id          UUID PRIMARY KEY,
    badge_id    UUID                        NOT NULL,
    action_type VARCHAR                     NOT NULL,
    timestamp        TIMESTAMP WITHOUT TIME ZONE NOT NULL
);


