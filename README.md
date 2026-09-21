# Sistema de Gestão de Viagens (SGV)

Aplicação web conteinerizada para gestão de viagens corporativas, com fluxo de
aprovação: o colaborador cadastra e submete uma viagem, e um gestor aprova,
rejeita ou solicita ajustes.

Arquitetura em três camadas separadas:

| Camada        | Tecnologia            | Porta |
|---------------|------------------------|-------|
| Frontend      | React + Vite (nginx)   | 3000  |
| Backend       | Spring Boot 3 (Java 21)| 8080  |
| Banco de dados| PostgreSQL 15          | 5432  |

## Como executar

### Com Docker (forma recomendada)

```bash
docker compose up --build
```

- Interface: http://localhost:3000
- API: http://localhost:8080/api/viagens

Os dados ficam no volume `pgdata` e sobrevivem ao reinício dos containers (RNF06).
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

## Fluxo de aprovação de viagens (Sprint 2)

1. O colaborador cadastra a viagem: ela nasce em **Rascunho** e pode ser editada
   ou excluída livremente.
2. Ao **submeter**, a viagem passa para **Solicitada** e não pode mais ser
   editada pelo colaborador — só o gestor pode agir sobre ela a partir daqui.
   O colaborador ainda pode **cancelar** uma viagem em Rascunho ou com Ajuste
   solicitado.
3. Na tela de viagens, o gestor analisa as Solicitadas e escolhe:
   - **Aprovar** — encerra o fluxo em **Aprovada**;
   - **Rejeitar** (com justificativa obrigatória) — encerra o fluxo em **Rejeitada**;
   - **Solicitar ajuste** (com justificativa obrigatória) — devolve a viagem ao
     colaborador em **Ajuste solicitado**, reabilitando a edição; o colaborador
     corrige e reenvia (volta a Solicitada).
4. Toda mudança de situação fica registrada no histórico da viagem: data,
   responsável e situação (mais a justificativa, quando houver).

Como o sistema não tem autenticação, quem está aprovando/rejeitando/ajustando
se identifica escolhendo o próprio nome num seletor de gestores no momento da
ação — o backend valida que o empregado escolhido realmente tem o cargo
"Gestor" (409 caso contrário).

A viagem também guarda uma fotografia da área e do cargo do empregado no
momento em que foi criada, preservando o histórico mesmo que o empregado mude
de área/cargo depois.

## Controle financeiro (Sprint 3)

Uma vez que a viagem está **Aprovada**, o colaborador pode registrar as
despesas do deslocamento (hospedagem, alimentação, transporte, combustível,
pedágios ou outras), informando data, tipo, descrição e valor. O sistema
calcula automaticamente o valor total gasto e disponibiliza um resumo
financeiro consolidado da viagem.

Regras de negócio: o valor da despesa deve ser maior que zero, a data não
pode ser futura, e viagens fora da situação Aprovada (incluindo as
Rejeitadas) não podem receber lançamentos de despesas.

## API REST

| Método | Rota                              | Descrição                                              | Respostas |
|--------|------------------------------------|---------------------------------------------------------|-----------|
| POST   | `/api/viagens`                     | Cadastra uma viagem em `Rascunho`                        | `201` / `400` |
| GET    | `/api/viagens`                     | Lista as viagens cadastradas                             | `200` |
| GET    | `/api/viagens/{id}`                | Consulta os dados completos de uma viagem                | `200` / `404` |
| PUT    | `/api/viagens/{id}`                | Altera uma viagem (Rascunho ou Ajuste solicitado)        | `200` / `400` / `404` / `409` |
| DELETE | `/api/viagens/{id}`                | Exclui uma viagem (somente em Rascunho)                  | `204` / `404` / `409` |
| POST   | `/api/viagens/{id}/submissao`      | Submete/reenvia para análise → Solicitada                | `200` / `404` / `409` |
| POST   | `/api/viagens/{id}/cancelamento`   | Cancela a viagem (Rascunho ou Ajuste solicitado)         | `200` / `404` / `409` |
| POST   | `/api/viagens/{id}/aprovacao`      | Gestor aprova a viagem Solicitada                        | `200` / `400` / `404` / `409` |
| POST   | `/api/viagens/{id}/rejeicao`       | Gestor rejeita a viagem Solicitada (justificativa)       | `200` / `400` / `404` / `409` |
| POST   | `/api/viagens/{id}/ajuste`         | Gestor solicita ajuste na viagem Solicitada (justificativa) | `200` / `400` / `404` / `409` |
| GET    | `/api/viagens/{id}/historico`      | Histórico de mudanças de situação da viagem              | `200` / `404` |
| POST   | `/api/empregados`                  | Cadastra um empregado (matrícula `XXXX-X`)               | `201` / `400` / `404` / `409` |
| GET    | `/api/empregados`                  | Lista os empregados                                      | `200` |
| PUT    | `/api/empregados/{id}`             | Altera nome/área/cargo (matrícula é imutável)             | `200` / `400` / `404` |
| POST   | `/api/areas`                       | Cadastra uma área                                        | `201` / `400` / `409` |
| GET    | `/api/areas`                       | Lista as áreas                                           | `200` |
| POST   | `/api/cargos`                      | Cadastra um cargo (ex.: Colaborador, Gestor)             | `201` / `400` / `409` |
| GET    | `/api/cargos`                      | Lista os cargos                                          | `200` |
| POST   | `/api/status-viagem`               | Cadastra um status de viagem                             | `201` / `400` / `409` |
| GET    | `/api/status-viagem`               | Lista os status de viagem                                | `200` |
| GET    | `/api/meios-transporte`            | Lista as opções de meio de transporte                    | `200` |
| POST   | `/api/viagens/{id}/despesas`       | Registra uma despesa em uma viagem Aprovada              | `201` / `400` / `404` / `409` |
| GET    | `/api/viagens/{id}/despesas`       | Lista as despesas lançadas na viagem                     | `200` / `404` |
| GET    | `/api/viagens/{id}/despesas/resumo`| Resumo financeiro consolidado (total gasto + despesas)   | `200` / `404` |
| GET    | `/api/tipos-despesa`               | Lista os tipos de despesa (Hospedagem, Alimentação etc.) | `200` |

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

Exemplo de aprovação (o `gestorId` precisa ser de um empregado com cargo "Gestor"):

```bash
curl -X POST http://localhost:8080/api/viagens/1/aprovacao \
  -H "Content-Type: application/json" \
  -d '{"gestorId": 2}'
```

Exemplo de lançamento de despesa (a viagem precisa estar Aprovada):

```bash
curl -X POST http://localhost:8080/api/viagens/1/despesas \
  -H "Content-Type: application/json" \
  -d '{
    "dataDespesa": "2026-09-11",
    "descricao": "Hotel Centro",
    "valor": 350.00,
    "tipoDespesaId": 1
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

As tabelas são versionadas com **Flyway**, em
[`backend/src/main/resources/db/migration`](backend/src/main/resources/db/migration).
As migrações rodam automaticamente na subida do backend. O modelo segue o MER
elaborado pela equipe (`Documentacao/sprint2/MER.pdf`): tabelas `viagem`,
`empregado`, `area`, `cargo`, `status_viagem`, `meio_transporte`,
`viagem_status_historico`, `tipo_despesa` e `despesa`.

Por segurança, as tabelas ficam em um schema dedicado (`sgv`), não no
`public` — schema padrão e compartilhado do banco, alvo comum de ataques de
"schema squatting". O [`database/init.sh`](database/init.sh) cria esse schema
e aplica o princípio do privilégio mínimo: revoga qualquer acesso implícito
do `PUBLIC` (todo mundo) ao banco e ao schema `public`, e concede ao usuário
da aplicação (`grupo_sgv_backend`) apenas as permissões necessárias — `USAGE`
e `CREATE` no schema `sgv`, e `SELECT`/`INSERT`/`UPDATE`/`DELETE` nas tabelas
nele contidas.

## Testes

```bash
cd backend && mvn test
```

Cobrem o cadastro e o fluxo completo de aprovação de viagens (submissão,
cancelamento, aprovação, rejeição, ajuste e reenvio), o registro de despesas
em viagens Aprovadas (valor positivo, data não futura, resumo financeiro),
os cadastros de apoio (Empregado, Área, Cargo, Status de Viagem, Meio de
Transporte, Tipo de Despesa) e as regras de negócio associadas (formato de
matrícula, período da viagem, restrição de cargo para aprovar).

## Estrutura

```
backend/
  src/main/java/br/unioeste/sgv/
    viagem/          # entidade, historico de status, repositório, serviço, controller e DTOs
    empregado/        # cadastro de empregados (matricula, area, cargo)
    area/, cargo/     # cadastros de apoio
    statusviagem/     # cadastro dos status possiveis de uma viagem
    meiotransporte/   # cadastro (somente leitura) dos meios de transporte
    tipodespesa/      # cadastro (somente leitura) dos tipos de despesa
    despesa/          # lancamento de despesas em viagens aprovadas (Controle Financeiro)
    common/          # tratamento de erros e configuração de CORS
  src/main/resources/db/migration/   # scripts de banco (Flyway)
frontend/
  src/components/    # ViagemForm/ViagemList, EmpregadoPanel, AreaPanel, CargoPanel, StatusViagemPanel,
                      # AcaoGestorForm, DespesasPanel, TipoDespesaPanel
  src/pages/         # uma página por cadastro/tela
  nginx.conf         # serve o build e faz proxy de /api para o backend
docker-compose.yml
Documentacao/
  sprint0/, sprint1/, sprint2/   # artefatos de cada sprint (visão, casos de uso, MER, requisitos)
```
