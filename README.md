# Sistema de Gestão de Viagens (SGV)

Aplicação web conteinerizada para gestão de viagens corporativas.
Arquitetura em três camadas separadas, conforme a Seção 9 da Especificação de Requisitos:

| Camada        | Tecnologia            | Porta |
|---------------|-----------------------|-------|
| Frontend      | React + Vite (nginx)  | 3000  |
| Backend       | Spring Boot 3 (Java 21) | 8080 |
| Banco de dados| PostgreSQL 15         | 5432  |

## Escopo desta branch (`feature/planejar-viagem`)

Módulo de Planejamento de Viagens (RF#1), com as duas funcionalidades iniciais:

- **Cadastro de viagem** (RF-CAD-001) — formulário com destino, período, motivo, meio de transporte e responsável.
- **Listagem de viagens** (RF-CON-002) — tabela ordenada das viagens mais recentes para as mais antigas.

Regras de negócio implementadas:

| Regra      | Descrição                                                     | Onde |
|------------|---------------------------------------------------------------|------|
| RN-CAD-001 | Toda viagem possui um responsável, definido na criação          | `Viagem` (coluna `updatable = false`) |
| RN-CAD-002 | Toda viagem é criada na situação `Rascunho`                     | `Viagem` (construtor) |
| RN-CAD-003 | Destino, datas, motivo e meio de transporte são obrigatórios    | `ViagemRequest` + `ViagemForm.jsx` |
| RN-CAD-004 | Data de retorno ≥ data de saída                                 | `ViagemRequest.isPeriodoValido()` + `ViagemForm.jsx` + `CHECK` no banco |
| RN-CON-002 | A listagem exibe destino, período, situação e responsável       | `ViagemResponse` / `ViagemList.jsx` |

Conforme RNF-ALT-001, as validações são aplicadas **no frontend e no backend**.

## Como executar

### Com Docker (forma recomendada)

```bash
docker compose up --build
```

- Interface: http://localhost:3000
- API: http://localhost:8080/api/viagens

Os dados ficam no volume `pgdata` e sobrevivem ao reinício dos containers (RNF-GLOB-002).
Para parar: `docker compose down` (use `docker compose down -v` para apagar também os dados).

### Sem Docker (desenvolvimento)

É necessário um PostgreSQL em `localhost:5432` com banco `sgv_db`, usuário `admin` e senha `adminpassword`.

```bash
# backend
cd backend && mvn spring-boot:run

# frontend (em outro terminal)
cd frontend && npm install && npm run dev
```

O Vite faz proxy de `/api` para `http://localhost:8080`, então não há configuração extra de URL.

## API REST

| Método | Rota           | Descrição                          | Respostas |
|--------|----------------|------------------------------------|-----------|
| POST   | `/api/viagens` | Cadastra uma viagem em `RASCUNHO`  | `201` / `400` com erros por campo |
| GET    | `/api/viagens` | Lista as viagens cadastradas       | `200` |

Exemplo de cadastro:

```bash
curl -X POST http://localhost:8080/api/viagens \
  -H "Content-Type: application/json" \
  -d '{
    "destino": "Curitiba - PR",
    "dataSaida": "2026-09-10",
    "dataRetorno": "2026-09-12",
    "motivo": "Reunião com cliente",
    "meioTransporte": "AEREO",
    "responsavel": "Carlos Penteado"
  }'
```

Resposta de erro de validação:

```json
{
  "timestamp": "2026-08-24T15:48:31.611-03:00",
  "status": 400,
  "mensagem": "Dados invalidos",
  "erros": {
    "destino": "O destino e obrigatorio",
    "periodoValido": "A data de retorno deve ser igual ou posterior a data de saida"
  }
}
```

## Banco de dados

O schema é versionado com **Flyway**: [`backend/src/main/resources/db/migration/V1__create_table_viagem.sql`](backend/src/main/resources/db/migration/V1__create_table_viagem.sql).
As migrações rodam automaticamente na subida do backend.

## Testes

```bash
cd backend && mvn test
```

Cobrem o cadastro válido, os campos obrigatórios (RN-CAD-003), a consistência do período (RN-CAD-004)
e a ordenação da listagem (RF-CON-002).

## Estrutura

```
backend/
  src/main/java/br/unioeste/sgv/
    viagem/          # entidade, enums, repositório, serviço, controller e DTOs
    common/          # tratamento de erros e configuração de CORS
  src/main/resources/db/migration/   # scripts de banco (Flyway)
frontend/
  src/components/    # ViagemForm (cadastro) e ViagemList (listagem)
  nginx.conf         # serve o build e faz proxy de /api para o backend
docker-compose.yml
```

## Próximos passos (fora do escopo desta entrega)

- Filtros de pesquisa por destino, período e situação (RN-CON-001)
- Alteração e exclusão de viagens em rascunho (RF-ALT-001 / RF-ALT-002)
- Submissão para análise (RF-SUB-001) e histórico de situações
- Autenticação: hoje o responsável é informado no formulário, pois o módulo de
  usuários ainda não existe. Quando houver autenticação, ele passa a vir do
  usuário logado, como pede a RN-CAD-001.
