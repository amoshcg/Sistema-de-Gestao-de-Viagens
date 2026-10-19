-- Cadastro de Empregados: fonte de onde a viagem obtem o nome do responsavel (RN-CAD-001 revisado).
CREATE TABLE empregado (
    id        BIGSERIAL    PRIMARY KEY,
    matricula VARCHAR(20)  NOT NULL,
    nome      VARCHAR(120) NOT NULL,
    area      VARCHAR(80)  NOT NULL,

    CONSTRAINT uk_empregado_matricula UNIQUE (matricula)
);

-- O responsavel pela viagem passa a ser um empregado cadastrado, nao mais texto livre.
-- Viagens existentes nao possuem empregado associado, entao a base e reiniciada (ambiente ainda em desenvolvimento).
TRUNCATE TABLE viagem;

ALTER TABLE viagem
    DROP COLUMN responsavel,
    ADD COLUMN empregado_id BIGINT NOT NULL REFERENCES empregado (id);

CREATE INDEX idx_viagem_empregado_id ON viagem (empregado_id);
