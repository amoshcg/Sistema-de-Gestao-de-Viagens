-- Modulo de Planejamento de Viagens (RF#1)
CREATE TABLE viagem (
    id              BIGSERIAL    PRIMARY KEY,
    destino         VARCHAR(120) NOT NULL,
    data_saida      DATE         NOT NULL,
    data_retorno    DATE         NOT NULL,
    motivo          VARCHAR(500) NOT NULL,
    meio_transporte VARCHAR(30)  NOT NULL,
    responsavel     VARCHAR(120) NOT NULL,
    situacao        VARCHAR(20)  NOT NULL DEFAULT 'RASCUNHO',
    criado_em       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    -- RN-CAD-004: a data de retorno deve ser igual ou posterior a data de saida.
    CONSTRAINT ck_viagem_periodo CHECK (data_retorno >= data_saida),
    -- Situacoes previstas na Especificacao, secao 4.
    CONSTRAINT ck_viagem_situacao CHECK (situacao IN ('RASCUNHO', 'SOLICITADA', 'APROVADA', 'REJEITADA')),
    CONSTRAINT ck_viagem_meio_transporte CHECK (meio_transporte IN ('AEREO', 'RODOVIARIO', 'VEICULO_PROPRIO'))
);

-- RF-CON-002: listagem priorizando as viagens mais recentes.
CREATE INDEX idx_viagem_criado_em ON viagem (criado_em DESC, id DESC);
