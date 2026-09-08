package br.unioeste.sgv.viagem;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.unioeste.sgv.area.Area;
import br.unioeste.sgv.area.AreaRepository;
import br.unioeste.sgv.cargo.Cargo;
import br.unioeste.sgv.cargo.CargoRepository;
import br.unioeste.sgv.empregado.Empregado;
import br.unioeste.sgv.empregado.EmpregadoRepository;
import br.unioeste.sgv.meiotransporte.MeioTransporte;
import br.unioeste.sgv.meiotransporte.MeioTransporteRepository;
import br.unioeste.sgv.statusviagem.StatusViagem;
import br.unioeste.sgv.statusviagem.StatusViagemRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ViagemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ViagemRepository repository;

    @Autowired
    private ViagemStatusHistoricoRepository historicoRepository;

    @Autowired
    private EmpregadoRepository empregadoRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private CargoRepository cargoRepository;

    @Autowired
    private MeioTransporteRepository meioTransporteRepository;

    @Autowired
    private StatusViagemRepository statusViagemRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Long empregadoId;
    private Long gestorId;
    private Long meioTransporteId;
    private Long outroMeioTransporteId;

    @BeforeEach
    void limparBase() {
        historicoRepository.deleteAll();
        repository.deleteAll();
        empregadoRepository.deleteAll();
        areaRepository.deleteAll();
        cargoRepository.deleteAll();
        meioTransporteRepository.deleteAll();
        statusViagemRepository.deleteAll();

        Area area = areaRepository.save(new Area("Comercial"));
        Cargo colaborador = cargoRepository.save(new Cargo("Colaborador"));
        Cargo gestor = cargoRepository.save(new Cargo("Gestor"));
        empregadoId = empregadoRepository.save(new Empregado("E001", "Carlos Penteado", area, colaborador)).getId();
        gestorId = empregadoRepository.save(new Empregado("E002", "Marcia Ribeiro", area, gestor)).getId();
        meioTransporteId = meioTransporteRepository.save(new MeioTransporte("Aereo")).getId();
        outroMeioTransporteId = meioTransporteRepository.save(new MeioTransporte("Rodoviario")).getId();
        for (String descricao : new String[] {"Rascunho", "Solicitada", "Aprovada", "Rejeitada", "Ajuste solicitado", "Cancelada"}) {
            statusViagemRepository.save(new StatusViagem(descricao));
        }
    }

    private String json(String destino, String saida, String retorno, String motivo,
                        Long meioTransporteId, Long empregadoId) {
        return """
                {
                  "destino": "%s",
                  "dataSaida": "%s",
                  "dataRetorno": "%s",
                  "motivo": "%s",
                  "meioTransporteId": %s,
                  "empregadoId": %s
                }
                """.formatted(destino, saida, retorno, motivo, meioTransporteId, empregadoId);
    }

    private String jsonEdicao(String destino, String saida, String retorno, String motivo, Long meioTransporteId) {
        return """
                {
                  "destino": "%s",
                  "dataSaida": "%s",
                  "dataRetorno": "%s",
                  "motivo": "%s",
                  "meioTransporteId": %s
                }
                """.formatted(destino, saida, retorno, motivo, meioTransporteId);
    }

    private String jsonGestor(Long gestorId) {
        return """
                {"gestorId": %s}
                """.formatted(gestorId);
    }

    private String jsonGestorJustificativa(Long gestorId, String justificativa) {
        return """
                {"gestorId": %s, "justificativa": "%s"}
                """.formatted(gestorId, justificativa);
    }

    private Long cadastrarViagem(String destino, String saida, String retorno, String motivo) throws Exception {
        MvcResult resultado = mockMvc.perform(post("/api/viagens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(destino, saida, retorno, motivo, meioTransporteId, empregadoId)))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode corpo = objectMapper.readTree(resultado.getResponse().getContentAsString());
        return corpo.get("id").asLong();
    }

    @Test
    @DisplayName("RF-CAD-001 / RN-CAD-002: viagem valida e criada na situacao Rascunho, vinculada ao empregado")
    void cadastraViagemValida() throws Exception {
        mockMvc.perform(post("/api/viagens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Curitiba", "2026-09-10", "2026-09-12",
                                "Reuniao com cliente", meioTransporteId, empregadoId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.numero").isNumber())
                .andExpect(jsonPath("$.destino").value("Curitiba"))
                .andExpect(jsonPath("$.situacaoDescricao").value("Rascunho"))
                .andExpect(jsonPath("$.meioTransporteId").value(meioTransporteId))
                .andExpect(jsonPath("$.meioTransporteDescricao").value("Aereo"))
                .andExpect(jsonPath("$.empregadoId").value(empregadoId))
                .andExpect(jsonPath("$.empregadoNome").value("Carlos Penteado"))
                .andExpect(jsonPath("$.empregadoAreaNome").value("Comercial"))
                .andExpect(jsonPath("$.areaSolicitanteNome").value("Comercial"))
                .andExpect(jsonPath("$.cargoSolicitanteNome").value("Colaborador"));
    }

    @Test
    @DisplayName("RN-CAD-001: cadastro com empregado inexistente retorna 404")
    void rejeitaEmpregadoInexistente() throws Exception {
        mockMvc.perform(post("/api/viagens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Curitiba", "2026-09-10", "2026-09-12",
                                "Reuniao com cliente", meioTransporteId, 999999L)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Cadastro com meio de transporte inexistente retorna 404")
    void rejeitaMeioTransporteInexistente() throws Exception {
        mockMvc.perform(post("/api/viagens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Curitiba", "2026-09-10", "2026-09-12",
                                "Reuniao com cliente", 999999L, empregadoId)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("RN-CAD-003: campos obrigatorios ausentes retornam 400 com erro por campo")
    void rejeitaCamposObrigatorios() throws Exception {
        mockMvc.perform(post("/api/viagens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"destino": "  ", "motivo": ""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.destino").exists())
                .andExpect(jsonPath("$.erros.motivo").exists())
                .andExpect(jsonPath("$.erros.dataSaida").exists())
                .andExpect(jsonPath("$.erros.dataRetorno").exists())
                .andExpect(jsonPath("$.erros.meioTransporteId").exists())
                .andExpect(jsonPath("$.erros.empregadoId").exists());
    }

    @Test
    @DisplayName("RN-CAD-004: data de retorno anterior a de saida retorna 400")
    void rejeitaPeriodoInvertido() throws Exception {
        mockMvc.perform(post("/api/viagens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Cascavel", "2026-09-20", "2026-09-18",
                                "Treinamento", meioTransporteId, empregadoId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.periodoValido").exists());
    }

    @Test
    @DisplayName("RN-CAD-004: retorno no mesmo dia da saida e aceito")
    void aceitaPeriodoDeUmDia() throws Exception {
        mockMvc.perform(post("/api/viagens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Toledo", "2026-09-20", "2026-09-20",
                                "Visita tecnica", meioTransporteId, empregadoId)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("RF-CON-002: listagem retorna as viagens mais recentes primeiro")
    void listaViagensMaisRecentesPrimeiro() throws Exception {
        mockMvc.perform(post("/api/viagens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Foz do Iguacu", "2026-09-01", "2026-09-03",
                                "Congresso", meioTransporteId, empregadoId)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/viagens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Sao Paulo", "2026-10-05", "2026-10-08",
                                "Evento", meioTransporteId, empregadoId)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/viagens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].destino").value("Sao Paulo"))
                .andExpect(jsonPath("$[1].destino").value("Foz do Iguacu"));
    }

    @Test
    @DisplayName("RF-CON-001: consulta de uma viagem especifica retorna os dados completos")
    void buscaViagemPorId() throws Exception {
        Long id = cadastrarViagem("Londrina", "2026-09-01", "2026-09-03", "Treinamento");

        mockMvc.perform(get("/api/viagens/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.destino").value("Londrina"));
    }

    @Test
    @DisplayName("Consulta de viagem inexistente retorna 404")
    void buscaViagemInexistenteRetorna404() throws Exception {
        mockMvc.perform(get("/api/viagens/{id}", 999999))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("RF-ALT-001: viagem em Rascunho pode ser alterada")
    void alteraViagemEmRascunho() throws Exception {
        Long id = cadastrarViagem("Maringa", "2026-09-01", "2026-09-03", "Treinamento");

        mockMvc.perform(put("/api/viagens/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonEdicao("Maringa - Centro", "2026-09-02", "2026-09-04",
                                "Treinamento avancado", outroMeioTransporteId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.destino").value("Maringa - Centro"))
                .andExpect(jsonPath("$.meioTransporteId").value(outroMeioTransporteId));
    }

    @Test
    @DisplayName("RN-ALT-001: viagem fora de Rascunho/Ajuste nao pode ser alterada")
    void naoAlteraViagemForaDeRascunho() throws Exception {
        Long id = cadastrarViagem("Cascavel", "2026-09-01", "2026-09-03", "Treinamento");
        mockMvc.perform(post("/api/viagens/{id}/submissao", id)).andExpect(status().isOk());

        mockMvc.perform(put("/api/viagens/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonEdicao("Cascavel - Centro", "2026-09-02", "2026-09-04",
                                "Treinamento avancado", outroMeioTransporteId)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("RF-ALT-002 / RN-ALT-003: viagem em Rascunho pode ser excluida definitivamente")
    void excluiViagemEmRascunho() throws Exception {
        Long id = cadastrarViagem("Guarapuava", "2026-09-01", "2026-09-03", "Treinamento");

        mockMvc.perform(delete("/api/viagens/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/viagens/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("RN-ALT-001: viagem fora de Rascunho nao pode ser excluida")
    void naoExcluiViagemForaDeRascunho() throws Exception {
        Long id = cadastrarViagem("Ponta Grossa", "2026-09-01", "2026-09-03", "Treinamento");
        mockMvc.perform(post("/api/viagens/{id}/submissao", id)).andExpect(status().isOk());

        mockMvc.perform(delete("/api/viagens/{id}", id))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("RF-SUB-001 / RN-SUB-001: viagem em Rascunho pode ser submetida e passa a Solicitada")
    void submeteViagemEmRascunho() throws Exception {
        Long id = cadastrarViagem("Curitiba", "2026-09-01", "2026-09-03", "Treinamento");

        mockMvc.perform(post("/api/viagens/{id}/submissao", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.situacaoDescricao").value("Solicitada"));
    }

    @Test
    @DisplayName("RN-SUB-001: viagem ja submetida nao pode ser submetida novamente")
    void naoSubmeteViagemDuasVezes() throws Exception {
        Long id = cadastrarViagem("Foz do Iguacu", "2026-09-01", "2026-09-03", "Treinamento");
        mockMvc.perform(post("/api/viagens/{id}/submissao", id)).andExpect(status().isOk());

        mockMvc.perform(post("/api/viagens/{id}/submissao", id))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Viagem em Rascunho pode ser cancelada, encerrando o fluxo")
    void cancelaViagemEmRascunho() throws Exception {
        Long id = cadastrarViagem("Umuarama", "2026-09-01", "2026-09-03", "Treinamento");

        mockMvc.perform(post("/api/viagens/{id}/cancelamento", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.situacaoDescricao").value("Cancelada"));
    }

    @Test
    @DisplayName("Viagem Solicitada nao pode ser cancelada diretamente")
    void naoCancelaViagemSolicitada() throws Exception {
        Long id = cadastrarViagem("Apucarana", "2026-09-01", "2026-09-03", "Treinamento");
        mockMvc.perform(post("/api/viagens/{id}/submissao", id)).andExpect(status().isOk());

        mockMvc.perform(post("/api/viagens/{id}/cancelamento", id))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Gestor aprova viagem Solicitada, encerrando o fluxo")
    void aprovaViagemSolicitada() throws Exception {
        Long id = cadastrarViagem("Curitiba", "2026-09-01", "2026-09-03", "Treinamento");
        mockMvc.perform(post("/api/viagens/{id}/submissao", id)).andExpect(status().isOk());

        mockMvc.perform(post("/api/viagens/{id}/aprovacao", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonGestor(gestorId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.situacaoDescricao").value("Aprovada"));
    }

    @Test
    @DisplayName("Viagem em Rascunho nao pode ser aprovada")
    void naoAprovaViagemForaDeSolicitada() throws Exception {
        Long id = cadastrarViagem("Cianorte", "2026-09-01", "2026-09-03", "Treinamento");

        mockMvc.perform(post("/api/viagens/{id}/aprovacao", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonGestor(gestorId)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Empregado sem cargo Gestor nao pode aprovar viagem")
    void naoAprovaComGestorInvalido() throws Exception {
        Long id = cadastrarViagem("Sarandi", "2026-09-01", "2026-09-03", "Treinamento");
        mockMvc.perform(post("/api/viagens/{id}/submissao", id)).andExpect(status().isOk());

        mockMvc.perform(post("/api/viagens/{id}/aprovacao", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonGestor(empregadoId)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Aprovar com gestor inexistente retorna 404")
    void naoAprovaComGestorInexistente() throws Exception {
        Long id = cadastrarViagem("Paranavai", "2026-09-01", "2026-09-03", "Treinamento");
        mockMvc.perform(post("/api/viagens/{id}/submissao", id)).andExpect(status().isOk());

        mockMvc.perform(post("/api/viagens/{id}/aprovacao", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonGestor(999999L)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Gestor rejeita viagem Solicitada, encerrando o fluxo")
    void rejeitaViagemSolicitada() throws Exception {
        Long id = cadastrarViagem("Campo Mourao", "2026-09-01", "2026-09-03", "Treinamento");
        mockMvc.perform(post("/api/viagens/{id}/submissao", id)).andExpect(status().isOk());

        mockMvc.perform(post("/api/viagens/{id}/rejeicao", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonGestorJustificativa(gestorId, "Fora do orcamento do trimestre")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.situacaoDescricao").value("Rejeitada"));
    }

    @Test
    @DisplayName("Rejeitar sem justificativa retorna 400")
    void rejeitaSemJustificativaRetorna400() throws Exception {
        Long id = cadastrarViagem("Ivaipora", "2026-09-01", "2026-09-03", "Treinamento");
        mockMvc.perform(post("/api/viagens/{id}/submissao", id)).andExpect(status().isOk());

        mockMvc.perform(post("/api/viagens/{id}/rejeicao", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonGestorJustificativa(gestorId, "")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.justificativa").exists());
    }

    @Test
    @DisplayName("Gestor solicita ajuste; viagem volta a ser editavel e pode ser reenviada")
    void solicitaAjusteEReenvia() throws Exception {
        Long id = cadastrarViagem("Telemaco Borba", "2026-09-01", "2026-09-03", "Treinamento");
        mockMvc.perform(post("/api/viagens/{id}/submissao", id)).andExpect(status().isOk());

        mockMvc.perform(post("/api/viagens/{id}/ajuste", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonGestorJustificativa(gestorId, "Detalhar melhor o motivo da viagem")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.situacaoDescricao").value("Ajuste solicitado"));

        mockMvc.perform(put("/api/viagens/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonEdicao("Telemaco Borba - Fabrica", "2026-09-01", "2026-09-03",
                                "Visita tecnica a fabrica de papel", meioTransporteId)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/viagens/{id}/submissao", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.situacaoDescricao").value("Solicitada"));

        mockMvc.perform(get("/api/viagens/{id}/historico", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].situacaoDescricao").value("Rascunho"))
                .andExpect(jsonPath("$[1].situacaoDescricao").value("Solicitada"))
                .andExpect(jsonPath("$[2].situacaoDescricao").value("Ajuste solicitado"))
                .andExpect(jsonPath("$[2].justificativa").value("Detalhar melhor o motivo da viagem"))
                .andExpect(jsonPath("$[2].responsavelId").value(gestorId))
                .andExpect(jsonPath("$[3].situacaoDescricao").value("Solicitada"))
                .andExpect(jsonPath("$[3].responsavelId").value(empregadoId));
    }

    @Test
    @DisplayName("Viagem com ajuste solicitado pode ser cancelada")
    void cancelaViagemComAjusteSolicitado() throws Exception {
        Long id = cadastrarViagem("Arapongas", "2026-09-01", "2026-09-03", "Treinamento");
        mockMvc.perform(post("/api/viagens/{id}/submissao", id)).andExpect(status().isOk());
        mockMvc.perform(post("/api/viagens/{id}/ajuste", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonGestorJustificativa(gestorId, "Revisar datas")))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/viagens/{id}/cancelamento", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.situacaoDescricao").value("Cancelada"));
    }

    @Test
    @DisplayName("GET /historico de uma viagem inexistente retorna 404")
    void historicoDeViagemInexistenteRetorna404() throws Exception {
        mockMvc.perform(get("/api/viagens/{id}/historico", 999999))
                .andExpect(status().isNotFound());
    }
}
