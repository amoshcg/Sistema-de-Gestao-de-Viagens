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

| Método | Rota                          | Descrição                          | Respostas |
|--------|-------------------------------|-------------------------------------|-----------|
| POST   | `/api/viagens`                | Cadastra uma viagem em `RASCUNHO`   | `201` / `400` com erros por campo |
| GET    | `/api/viagens`                | Lista as viagens cadastradas        | `200` |
| GET    | `/api/viagens/{id}`           | Consulta os dados completos de uma viagem | `200` / `404` |
| PUT    | `/api/viagens/{id}`           | Altera uma viagem (somente em `RASCUNHO`) | `200` / `400` / `404` / `409` |
| DELETE | `/api/viagens/{id}`           | Exclui uma viagem (somente em `RASCUNHO`) | `204` / `404` / `409` |
| POST   | `/api/viagens/{id}/submissao` | Submete para análise: `RASCUNHO` → `SOLICITADA` | `200` / `404` / `409` |
| POST   | `/api/empregados`             | Cadastra um empregado               | `201` / `400` / `404` / `409` |
| GET    | `/api/empregados`             | Lista os empregados                 | `200` |
| POST   | `/api/areas`                  | Cadastra uma área                   | `201` / `400` / `409` |
| GET    | `/api/areas`                  | Lista as áreas                      | `200` |
| GET    | `/api/meios-transporte`       | Lista as opções de meio de transporte | `200` |

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
