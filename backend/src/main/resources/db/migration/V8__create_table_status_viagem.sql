-- Sprint 2: modela o status da viagem como tabela propria (modelo ER da equipe), com tela de
-- cadastro (RH: "Manter Status de Viagem"), em vez do enum + CHECK usado ate aqui.
CREATE TABLE status_viagem (
    id        BIGSERIAL   PRIMARY KEY,
    descricao VARCHAR(50) NOT NULL,

    CONSTRAINT uk_status_viagem_descricao UNIQUE (descricao)
);

INSERT INTO status_viagem (descricao) VALUES
    ('Rascunho'),
    ('Solicitada'),
    ('Aprovada'),
    ('Rejeitada'),
    ('Ajuste solicitado'),
    ('Cancelada');

ALTER TABLE viagem ADD COLUMN status_viagem_id BIGINT;

UPDATE viagem v SET status_viagem_id = sv.id
    FROM status_viagem sv
    WHERE (v.situacao = 'RASCUNHO' AND sv.descricao = 'Rascunho')
       OR (v.situacao = 'SOLICITADA' AND sv.descricao = 'Solicitada')
       OR (v.situacao = 'APROVADA' AND sv.descricao = 'Aprovada')
       OR (v.situacao = 'REJEITADA' AND sv.descricao = 'Rejeitada');

ALTER TABLE viagem
    DROP CONSTRAINT ck_viagem_situacao,
    ALTER COLUMN status_viagem_id SET NOT NULL,
    ADD CONSTRAINT fk_viagem_status_viagem FOREIGN KEY (status_viagem_id) REFERENCES status_viagem (id),
    DROP COLUMN situacao;

CREATE INDEX idx_viagem_status_viagem_id ON viagem (status_viagem_id);
