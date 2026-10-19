-- Cadastro de Cargos: usado para classificar os Empregados (ex: Colaborador, Gestor).
CREATE TABLE cargo (
    id   BIGSERIAL   PRIMARY KEY,
    nome VARCHAR(45) NOT NULL,

    CONSTRAINT uk_cargo_nome UNIQUE (nome)
);

INSERT INTO cargo (nome) VALUES
    ('Colaborador'),
    ('Gestor');
