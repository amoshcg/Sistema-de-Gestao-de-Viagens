-- Normaliza area e meio_transporte em tabelas de referencia proprias (modelo ER fornecido pela equipe).

CREATE TABLE area (
    id   BIGSERIAL   PRIMARY KEY,
    nome VARCHAR(45) NOT NULL,

    CONSTRAINT uk_area_nome UNIQUE (nome)
);

INSERT INTO area (nome)
SELECT DISTINCT area FROM empregado;

ALTER TABLE empregado ADD COLUMN area_id BIGINT;
UPDATE empregado e SET area_id = a.id FROM area a WHERE a.nome = e.area;
ALTER TABLE empregado
    ALTER COLUMN area_id SET NOT NULL,
    ADD CONSTRAINT fk_empregado_area FOREIGN KEY (area_id) REFERENCES area (id),
    DROP COLUMN area;

CREATE INDEX idx_empregado_area_id ON empregado (area_id);

CREATE TABLE meio_transporte (
    id        BIGSERIAL   PRIMARY KEY,
    descricao VARCHAR(60) NOT NULL,

    CONSTRAINT uk_meio_transporte_descricao UNIQUE (descricao)
);

-- RF-CAD-001 (observacao): opcoes pre-definidas de meio de transporte.
INSERT INTO meio_transporte (descricao) VALUES
    ('Aereo'),
    ('Rodoviario'),
    ('Veiculo proprio');

ALTER TABLE viagem ADD COLUMN meio_transporte_id BIGINT;
UPDATE viagem v SET meio_transporte_id = mt.id
    FROM meio_transporte mt
    WHERE (v.meio_transporte = 'AEREO' AND mt.descricao = 'Aereo')
       OR (v.meio_transporte = 'RODOVIARIO' AND mt.descricao = 'Rodoviario')
       OR (v.meio_transporte = 'VEICULO_PROPRIO' AND mt.descricao = 'Veiculo proprio');

ALTER TABLE viagem
    DROP CONSTRAINT ck_viagem_meio_transporte,
    ALTER COLUMN meio_transporte_id SET NOT NULL,
    ADD CONSTRAINT fk_viagem_meio_transporte FOREIGN KEY (meio_transporte_id) REFERENCES meio_transporte (id),
    DROP COLUMN meio_transporte;

CREATE INDEX idx_viagem_meio_transporte_id ON viagem (meio_transporte_id);
