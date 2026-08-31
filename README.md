# Sistema de Gestão de Viagens (SGV)

Aplicação web conteinerizada para gestão de viagens corporativas.
Arquitetura em três camadas separadas:

| Camada        | Tecnologia            | Porta |
|---------------|-----------------------|-------|
| Frontend      | React + Vite (nginx)  | 3000  |
| Backend       | Spring Boot 3 (Java 21) | 8080 |
| Banco de dados| PostgreSQL 15         | 5432  |

## Como executar

### Com Docker (forma recomendada)

Crie um arquivo `.env` na raiz do projeto (não é versionado) com as credenciais do banco:

```bash
POSTGRES_USER=admin
POSTGRES_PASSWORD=adminpassword
API_DB_USER=sgv_backend
API_DB_PASSWORD=sgv_backend_password
```

Depois suba os containers:

```bash
docker compose up --build
```

- Interface: http://localhost:3000
- API: http://localhost:8080/api

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

## Frontend

A interface é organizada em uma rota por menu (React Router):

| Rota                 | Página                                            |
|----------------------|----------------------------------------------------|
| `/viagens`           | Cadastro, edição, submissão e exclusão de viagens   |
| `/empregados`        | Cadastro e listagem de empregados                   |
| `/areas`             | Cadastro e listagem de áreas                        |
| `/meios-transporte`  | Consulta das opções de meio de transporte (somente leitura) |

## API REST

Os cadastros de apoio (Área, Empregado, Meio de transporte) precisam existir **antes** de cadastrar uma viagem,
já que ela referencia um Empregado e um Meio de transporte já cadastrados.

| Método | Rota                          | Descrição                                        | Respostas |
|--------|-------------------------------|---------------------------------------------------|-----------|
| POST   | `/api/areas`                  | Cadastra uma área                                  | `201` / `400` / `409` |
| GET    | `/api/areas`                  | Lista as áreas                                     | `200` |
| POST   | `/api/empregados`             | Cadastra um empregado                              | `201` / `400` / `404` / `409` |
| GET    | `/api/empregados`             | Lista os empregados                                | `200` |
| GET    | `/api/meios-transporte`       | Lista as opções de meio de transporte              | `200` |
| POST   | `/api/viagens`                | Cadastra uma viagem em `RASCUNHO`                  | `201` / `400` / `404` |
| GET    | `/api/viagens`                | Lista as viagens cadastradas                       | `200` |
| GET    | `/api/viagens/{id}`           | Consulta os dados completos de uma viagem          | `200` / `404` |
| PUT    | `/api/viagens/{id}`           | Altera uma viagem (somente em `RASCUNHO`)          | `200` / `400` / `404` / `409` |
| DELETE | `/api/viagens/{id}`           | Exclui uma viagem (somente em `RASCUNHO`)          | `204` / `404` / `409` |
| POST   | `/api/viagens/{id}/submissao` | Submete para análise: `RASCUNHO` → `SOLICITADA`    | `200` / `404` / `409` |

O número da viagem (`numero`) e o empregado responsável são atribuídos na criação e não podem ser alterados depois.

Exemplo de cadastro:

```bash
curl -X POST http://localhost:8080/api/viagens \
  -H "Content-Type: application/json" \
  -d '{
    "destino": "Curitiba - PR",
    "dataSaida": "2026-09-10",
    "dataRetorno": "2026-09-12",
    "motivo": "Reunião com cliente",
    "meioTransporteId": 1,
    "empregadoId": 1
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

Resposta de conflito de estado (ex.: alterar uma viagem já submetida):

```json
{
  "timestamp": "2026-08-24T15:48:31.611-03:00",
  "status": 409,
  "mensagem": "Somente viagens em Rascunho podem ser alteradas",
  "erros": {}
}
```

## Banco de dados

O schema é versionado com **Flyway** em [`backend/src/main/resources/db/migration`](backend/src/main/resources/db/migration):

| Migração | Descrição |
|----------|-----------|
| `V1__create_table_viagem.sql` | Cria a tabela `viagem` |
| `V2__create_table_empregado_e_vincular_viagem.sql` | Cria `empregado` e vincula a viagem por `empregado_id` |
| `V3__seed_empregados.sql` | Massa inicial de empregados |
| `V4__normaliza_area_e_meio_transporte.sql` | Extrai `area` e `meio_transporte` para tabelas próprias |

As migrações rodam automaticamente na subida do backend.

## Testes

```bash
cd backend && mvn test
```

Cobrem cadastro, consulta, alteração, exclusão e submissão de viagens (RN-CAD-002 a 004, RN-ALT-001, RN-SUB-001),
além do cadastro e das regras de unicidade de Empregados, Áreas e Meios de transporte.

## Estrutura

```
backend/
  src/main/java/br/unioeste/sgv/
    viagem/          # entidade, repositório, serviço, controller e DTOs da viagem
    empregado/       # cadastro de empregados
    area/            # cadastro de áreas
    meiotransporte/  # opções de meio de transporte (lookup)
    common/          # tratamento de erros e configuração de CORS
  src/main/resources/db/migration/   # scripts de banco (Flyway)
frontend/
  src/pages/         # uma página por menu (Viagens, Empregados, Áreas, Meios de transporte)
  src/components/    # formulários, listagens e o menu de navegação
  nginx.conf         # serve o build, faz proxy de /api para o backend e fallback de SPA
docker-compose.yml
```
