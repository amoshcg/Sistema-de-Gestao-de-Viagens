-- Vincula cada empregado a um cargo (Sprint 2: modulo de aprovacao de viagens).
-- Empregados existentes assumem Colaborador ate que sejam atualizados pela tela de cadastro.
ALTER TABLE empregado ADD COLUMN cargo_id BIGINT;

UPDATE empregado SET cargo_id = (SELECT id FROM cargo WHERE nome = 'Colaborador');

ALTER TABLE empregado
    ALTER COLUMN cargo_id SET NOT NULL,
    ADD CONSTRAINT fk_empregado_cargo FOREIGN KEY (cargo_id) REFERENCES cargo (id);

CREATE INDEX idx_empregado_cargo_id ON empregado (cargo_id);
