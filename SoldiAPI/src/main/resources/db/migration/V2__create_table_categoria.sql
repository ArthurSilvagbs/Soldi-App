CREATE TABLE categoria (
    id_categoria UUID NOT NULL,
    id_user UUID NOT NULL,
    nome VARCHAR (255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT pk_categoria PRIMARY KEY (id_categoria),
    CONSTRAINT fk_categoria_id_user FOREIGN KEY (id_user) REFERENCES users (id_user)
);