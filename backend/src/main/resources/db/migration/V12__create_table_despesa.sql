-- Sprint 3: Modulo de Controle Financeiro (RF#3). Despesas lancadas pelo colaborador em
-- viagens Aprovadas (RN: valor > 0, data nao futura; viagem precisa estar Aprovada).
CREATE TABLE despesa (
    id              BIGSERIAL      PRIMARY KEY,
    data_despesa    DATE           NOT NULL,
    descricao       VARCHAR(255)   NOT NULL,
    valor           NUMERIC(10,2)  NOT NULL,
    viagem_id       BIGINT         NOT NULL REFERENCES viagem (id),
    tipo_despesa_id BIGINT         NOT NULL REFERENCES tipo_despesa (id),
    criado_em       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT ck_despesa_valor_positivo CHECK (valor > 0),
    CONSTRAINT ck_despesa_data_nao_futura CHECK (data_despesa <= CURRENT_DATE)
);

CREATE INDEX idx_despesa_viagem_id ON despesa (viagem_id);
