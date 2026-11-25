CREATE TABLE users (
    id_user UUID NOT NULL,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(75) NOT NULL,
    senha_hash VARCHAR(60) NOT NULL,
    roles VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT pk_users PRIMARY KEY (id_user),
    CONSTRAINT uk_user_email UNIQUE (email),
    CONSTRAINT ck_users_roles CHECK (
        roles IS NULL
        OR roles IN (
            'ADMIN',
            'USER'
        )
    )
);