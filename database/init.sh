#!/bin/bash
# O comando set -e faz com que o script pare se der algum erro
set -e

# O psql é o terminal do Postgres. Ele loga como o administrador e roda o bloco de código abaixo (EOSQL)
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL

    -- 1. Estabelecer o Grupo de Acesso (Role)
    CREATE ROLE grupo_sgv_backend;

    -- 2. Conceder permissões adequadas
    GRANT CONNECT ON DATABASE $POSTGRES_DB TO grupo_sgv_backend;
    GRANT USAGE, CREATE ON SCHEMA public TO grupo_sgv_backend;
    ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO grupo_sgv_backend;

    -- 3. Criar o usuário injetando as variáveis do .env dinamicamente
    CREATE USER $API_DB_USER WITH PASSWORD '$API_DB_PASSWORD';

    -- 4. Associar o usuário ao grupo
    GRANT grupo_sgv_backend TO $API_DB_USER;

EOSQL