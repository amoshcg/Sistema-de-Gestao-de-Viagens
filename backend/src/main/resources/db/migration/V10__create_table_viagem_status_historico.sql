-- Sprint 2: historico das mudancas de status de uma viagem (data, responsavel e status).
CREATE TABLE viagem_status_historico (
    id             BIGSERIAL   PRIMARY KEY,
    viagem_id      BIGINT      NOT NULL,
    status_viagem_id BIGINT    NOT NULL,
    data_mudanca   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    responsavel_id BIGINT      NOT NULL,
    justificativa  VARCHAR(500),

    CONSTRAINT fk_historico_viagem FOREIGN KEY (viagem_id) REFERENCES viagem (id),
    CONSTRAINT fk_historico_status_viagem FOREIGN KEY (status_viagem_id) REFERENCES status_viagem (id),
    CONSTRAINT fk_historico_responsavel FOREIGN KEY (responsavel_id) REFERENCES empregado (id)
);

CREATE INDEX idx_historico_viagem_data ON viagem_status_historico (viagem_id, data_mudanca);

-- Backfill: uma linha de historico para cada viagem ja existente, refletindo seu estado atual.
INSERT INTO viagem_status_historico (viagem_id, status_viagem_id, data_mudanca, responsavel_id, justificativa)
SELECT id, status_viagem_id, criado_em, empregado_id, NULL
FROM viagem;
