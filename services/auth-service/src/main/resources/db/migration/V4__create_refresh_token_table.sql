CREATE TABLE auth.refresh_tokens
(
    id UUID PRIMARY KEY,

    token VARCHAR(500) NOT NULL UNIQUE,

    user_id UUID NOT NULL UNIQUE,

    expiry_date TIMESTAMP NOT NULL,

    revoked BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_refresh_user
        FOREIGN KEY (user_id)
        REFERENCES auth.users(id)
        ON DELETE CASCADE
);