-- Sprint 3: Modulo de Controle Financeiro (RF#3). Tipos de despesa pre-definidos,
-- nos mesmos moldes do cadastro de meio_transporte (lista fixa, mantida via migracao).
CREATE TABLE tipo_despesa (
    id   BIGSERIAL   PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,

    CONSTRAINT uk_tipo_despesa_nome UNIQUE (nome)
);

INSERT INTO tipo_despesa (nome) VALUES
    ('Hospedagem'),
    ('Alimentação'),
    ('Transporte'),
    ('Combustível'),
    ('Pedágios'),
    ('Outras despesas');
