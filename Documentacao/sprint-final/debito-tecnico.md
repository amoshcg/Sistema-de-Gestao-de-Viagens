# Débito técnico — Sprint final

Levantamento feito lendo o código em `feature/dashboard` (base: `feature/registrar-despesas`,
que reúne o trabalho de todas as sprints anteriores). Cada item abaixo está pronto para virar
um card no kanban: título, descrição, juros, classificação, esforço e definição de pronto (DoD).

Classificação segundo a matriz de Martin Fowler (deliberado/inadvertido × prudente/imprudente).

---

## 1. Autorização de gestor por string mágica e sem autenticação real

**Descrição:** Não existe login nem sessão no sistema. Toda ação de gestor (`POST
/api/viagens/{id}/aprovacao`, `/rejeicao`, `/ajuste`) recebe um `gestorId` enviado livremente
pelo cliente (`ViagemService.java:191-198`), e a verificação de que o empregado é de fato um
gestor compara o nome do cargo como texto (`"Gestor".equalsIgnoreCase(...)`,
`ViagemService.java:194`). Qualquer chamada à API pode se passar por qualquer empregado ou
gestor só informando o ID.

- **Classificação:** Deliberado e prudente — a disciplina não pede autenticação nos requisitos,
  e a equipe optou conscientemente por adiar esse trabalho para focar nos RFs de negócio de cada
  sprint.
- **Juros:** Alto. Qualquer cliente da API aprova/rejeita viagens e lança despesas em nome de
  outra pessoa; renomear o cargo "Gestor" no cadastro quebra a autorização silenciosamente, sem
  erro de compilação nem teste que acuse.
- **Esforço:** Alto (~1-2 sprints: Spring Security, login, obter a identidade do usuário do
  contexto de segurança em vez do corpo da requisição, revisar todos os endpoints e telas).
- **Definição de pronto:** Login funcional; `gestorId`/`empregadoId` deixam de ser enviados pelo
  cliente nas ações sensíveis e passam a vir do usuário autenticado; a checagem de cargo usa um
  campo estruturado (enum/flag), não comparação de string; testes de autorização cobrindo
  aprovação, rejeição e ajuste com usuário sem permissão.

---

## 2. ~~Pesquisa de viagens (RF da seção 6 da especificação) nunca foi implementada~~ — RESOLVIDO

**Descrição:** A especificação pede filtros de destino, período e situação na consulta de
viagens desde a Sprint 0. `GET /api/viagens` sempre retornava a lista inteira, sem nenhum
parâmetro de busca; o frontend também não tinha campo de filtro.

- **Classificação:** Inadvertido e imprudente — o requisito já estava documentado e nenhuma
  sprint percebeu a lacuna ao entregar cadastro/aprovação/despesas.
- **Juros:** Médio, crescendo com o volume de viagens cadastradas: sem filtro (e sem paginação,
  ver item 3) a tela de consulta deixa de atender ao caso de uso original conforme a base cresce.
- **Esforço:** Médio (query params no endpoint + índices no banco + campo de busca na tela).
- **Definição de pronto:** `GET /api/viagens` aceita `destino`, `dataInicio`/`dataFim` e
  `situacao` como filtros opcionais combináveis; a tela de viagens tem um formulário de busca;
  testes cobrindo cada filtro isolado e combinado.
- **Resolvido nesta sprint:** filtros implementados via `Specification`
  (`ViagemSpecifications.java`), cada resultado já traz o valor gasto na viagem, e a tela de
  Viagens ganhou o formulário "Pesquisar viagens" e a coluna "Total gasto". Durante a
  implementação foi descoberto e corrigido um bug real contra o Postgres (não pego pelos testes,
  que rodam em H2): o padrão `"(:param is null or ...)"` em JPQL faz o driver do Postgres tentar
  inferir o tipo de parâmetros nulos a partir do texto da consulta preparada, e falha
  (`could not determine data type of parameter` / `cannot cast type bytea to ...`) assim que a
  consulta é reaproveitada pelo servidor — daí a escolha por `Specification`, que não gera
  predicado nem bind para um filtro ausente.

---

## 3. Listagens sem paginação

**Descrição:** Todos os endpoints de listagem (`ViagemRepository.findAllByOrderByCriadoEmDescIdDesc`,
`EmpregadoRepository`, `DespesaRepository.findAllByViagemIdOrderByDataDespesaAscIdAsc`, e o novo
`DashboardService.indicadores()` que faz `viagemRepository.findAll()`) carregam a tabela inteira
na memória a cada chamada.

- **Classificação:** Inadvertido e prudente — razoável para o volume de dados de um projeto
  acadêmico; ninguém decidiu conscientemente não paginar, simplesmente não era um problema até
  agora.
- **Juros:** Baixo hoje; cresce de forma proporcional ao número de viagens/despesas cadastradas
  (mais visível no dashboard, que hoje percorre todas as viagens em memória para calcular contagens
  e o destino mais visitado).
- **Esforço:** Médio (introduzir `Pageable` nos repositórios e endpoints, ajustar contrato da API
  e a tabela no frontend).
- **Definição de pronto:** Endpoints de listagem aceitam `page`/`size`; resposta inclui metadados
  de paginação; a tabela de viagens no frontend pagina os resultados.

---

## 4. Regra de "custo de deslocamento" hardcoded no código em vez de configurável

**Descrição:** O novo cálculo de custos por categoria (Sprint final, item a) agrupa despesas em
"deslocamento" a partir de uma lista fixa de nomes de tipo no código
(`DespesaService.CATEGORIAS_DESLOCAMENTO = List.of("Transporte", "Combustível", "Pedágios")`).
Se alguém cadastrar um novo tipo de despesa (ex.: "Uber", "Passagem rodoviária") pelo cadastro de
Tipos de Despesa, ele cai fora do cálculo de custo total até alguém lembrar de atualizar essa
lista no código.

- **Classificação:** Deliberado e prudente — decisão tomada nesta sprint por não existir uma
  categoria "Deslocamento" explícita nos dados já cadastrados; documentada no código
  (`DespesaService.java`), mas pendente de validação com o cliente sobre quais tipos entram em
  cada grupo.
- **Juros:** Médio — cresce a cada novo tipo de despesa cadastrado sem atualização da lista.
- **Esforço:** Baixo (adicionar um campo de categoria de custo ao cadastro de Tipo de Despesa em
  vez de uma lista fixa no código).
- **Definição de pronto:** Tipo de despesa passa a ter uma categoria de custo cadastrável
  (deslocamento/hospedagem/táxi/outros) pela UI; o cálculo de custos consulta essa categoria em
  vez da lista hardcoded; migração de dados para os tipos já existentes.

---

## 5. Agregação do dashboard feita em memória, sem consultas agregadas no banco

**Descrição:** `DashboardService.indicadores()` traz todas as viagens com `findAll()` e usa
streams Java para contar aprovadas/rejeitadas e achar o destino mais visitado, em vez de deixar o
banco fazer `COUNT`/`GROUP BY`.

- **Classificação:** Deliberado e prudente — decisão consciente para entregar a Sprint final no
  prazo, dado o volume atual de dados (dezenas de viagens); registrada aqui para revisão antes de
  qualquer uso com uma base maior.
- **Juros:** Baixo agora, cresce proporcionalmente ao número de viagens.
- **Esforço:** Baixo-médio (trocar por `@Query` com `COUNT`/`GROUP BY` nativos no
  `ViagemRepository`).
- **Definição de pronto:** `DashboardService` não carrega mais a lista completa de viagens em
  memória; indicadores calculados via consulta agregada no banco; teste garantindo o mesmo
  resultado de antes.

---

## 6. Setup de teste duplicado entre as classes de `*ControllerTest`

**Descrição:** O bloco `@BeforeEach limparBase()` (deletar todos os repositórios e recriar
área/cargo/empregado/status/tipo de despesa de teste) está copiado quase palavra por palavra em
`DespesaControllerTest`, `DashboardControllerTest`, `ViagemControllerTest` e outros. Uma nova
entidade obrigatória no domínio exige editar o mesmo bloco em ~8 arquivos.

- **Classificação:** Inadvertido e prudente — cada teste foi copiado do anterior por
  conveniência, sem que isso incomodasse até o número de testes crescer.
- **Juros:** Baixo — só afeta quem mantém os testes, mas cresce a cada nova entidade obrigatória
  no domínio.
- **Esforço:** Baixo (extrair um helper/fixture compartilhado, ex. um `@TestConfiguration` ou
  builder de massa de teste).
- **Definição de pronto:** Um único helper de setup reaproveitado pelos testes de controller, sem
  perder o isolamento entre testes (`@DirtiesContext` continua funcionando).

---

### Como usar este documento

Cada seção acima corresponde a um card do kanban: use o título como nome do card e o restante da
seção como descrição/campos. A tag Git `technical-debt` marca o commit em que este levantamento
foi feito, para referência futura.
