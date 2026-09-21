#!/bin/bash
# O comando set -e faz com que o script pare se der algum erro
set -e

# O psql é o terminal do Postgres. Ele loga como o administrador e roda o bloco de código abaixo (EOSQL)
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL

    -- 1. Criar um schema dedicado para a aplicação em vez de usar o "public"
    --    (schema padrão e compartilhado do banco: alvo comum de ataques de
    --    "schema squatting"/sequestro de objetos e, em versões antigas do
    --    Postgres, acessível por qualquer role por padrão).
    CREATE SCHEMA IF NOT EXISTS sgv;

    -- 2. Privilégio mínimo: remover qualquer acesso implícito do PUBLIC
    --    (todo mundo) ao banco e ao schema public, já que nada deve ser
    --    criado ou acessado neles.
    REVOKE ALL ON DATABASE $POSTGRES_DB FROM PUBLIC;
    REVOKE ALL ON SCHEMA public FROM PUBLIC;

    -- 3. Estabelecer o Grupo de Acesso (Role)
    CREATE ROLE grupo_sgv_backend;

    -- 4. Conceder apenas as permissões necessárias, restritas ao schema "sgv"
    GRANT CONNECT ON DATABASE $POSTGRES_DB TO grupo_sgv_backend;
    GRANT USAGE, CREATE ON SCHEMA sgv TO grupo_sgv_backend;
    ALTER DEFAULT PRIVILEGES IN SCHEMA sgv GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO grupo_sgv_backend;
    ALTER DEFAULT PRIVILEGES IN SCHEMA sgv GRANT USAGE, SELECT ON SEQUENCES TO grupo_sgv_backend;

    -- 5. Criar o usuário injetando as variáveis do .env dinamicamente, já
    --    com o schema da aplicação como padrão de busca
    CREATE USER $API_DB_USER WITH PASSWORD '$API_DB_PASSWORD';
    ALTER ROLE $API_DB_USER SET search_path TO sgv;

    -- 6. Associar o usuário ao grupo
    GRANT grupo_sgv_backend TO $API_DB_USER;

EOSQL