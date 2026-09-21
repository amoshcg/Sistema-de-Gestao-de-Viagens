package br.unioeste.sgv.dashboard;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.unioeste.sgv.area.Area;
import br.unioeste.sgv.area.AreaRepository;
import br.unioeste.sgv.cargo.Cargo;
import br.unioeste.sgv.cargo.CargoRepository;
import br.unioeste.sgv.despesa.DespesaRepository;
import br.unioeste.sgv.empregado.Empregado;
import br.unioeste.sgv.empregado.EmpregadoRepository;
import br.unioeste.sgv.meiotransporte.MeioTransporte;
import br.unioeste.sgv.meiotransporte.MeioTransporteRepository;
import br.unioeste.sgv.statusviagem.StatusViagem;
import br.unioeste.sgv.statusviagem.StatusViagemRepository;
import br.unioeste.sgv.tipodespesa.TipoDespesa;
import br.unioeste.sgv.tipodespesa.TipoDespesaRepository;
import br.unioeste.sgv.viagem.ViagemRepository;
import br.unioeste.sgv.viagem.ViagemStatusHistoricoRepository;
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
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ViagemRepository viagemRepository;

    @Autowired
    private ViagemStatusHistoricoRepository historicoRepository;

    @Autowired
    private DespesaRepository despesaRepository;

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

    @Autowired
    private TipoDespesaRepository tipoDespesaRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Long empregadoId;
    private Long gestorId;
    private Long meioTransporteId;
    private Long tipoDespesaId;

    @BeforeEach
    void limparBase() {
        despesaRepository.deleteAll();
        historicoRepository.deleteAll();
        viagemRepository.deleteAll();
        empregadoRepository.deleteAll();
        areaRepository.deleteAll();
        cargoRepository.deleteAll();
        meioTransporteRepository.deleteAll();
        statusViagemRepository.deleteAll();
        tipoDespesaRepository.deleteAll();

        Area area = areaRepository.save(new Area("Comercial"));
        Cargo colaborador = cargoRepository.save(new Cargo("Colaborador"));
        Cargo gestor = cargoRepository.save(new Cargo("Gestor"));
        empregadoId = empregadoRepository.save(new Empregado("0001-1", "Carlos Penteado", area, colaborador)).getId();
        gestorId = empregadoRepository.save(new Empregado("0002-2", "Marcia Ribeiro", area, gestor)).getId();
        meioTransporteId = meioTransporteRepository.save(new MeioTransporte("Aereo")).getId();
        for (String descricao : new String[] {"Rascunho", "Solicitada", "Aprovada", "Rejeitada", "Ajuste solicitado", "Cancelada"}) {
            statusViagemRepository.save(new StatusViagem(descricao));
        }
        tipoDespesaId = tipoDespesaRepository.save(new TipoDespesa("Hospedagem")).getId();
    }

    private Long cadastrarViagem(String destino) throws Exception {
        String corpo = """
                {
                  "destino": "%s",
                  "dataSaida": "2026-09-10",
                  "dataRetorno": "2026-09-12",
                  "motivo": "Reuniao com cliente",
                  "meioTransporteId": %s,
                  "empregadoId": %s
                }
                """.formatted(destino, meioTransporteId, empregadoId);
        MvcResult resultado = mockMvc.perform(post("/api/viagens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode json = objectMapper.readTree(resultado.getResponse().getContentAsString());
        return json.get("id").asLong();
    }

    private void submeter(Long viagemId) throws Exception {
        mockMvc.perform(post("/api/viagens/" + viagemId + "/submissao")).andExpect(status().isOk());
    }

    private void aprovar(Long viagemId) throws Exception {
        mockMvc.perform(post("/api/viagens/" + viagemId + "/aprovacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"gestorId": %s}
                                """.formatted(gestorId)))
                .andExpect(status().isOk());
    }

    private void rejeitar(Long viagemId) throws Exception {
        mockMvc.perform(post("/api/viagens/" + viagemId + "/rejeicao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"gestorId": %s, "justificativa": "Sem orcamento"}
                                """.formatted(gestorId)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Consolida os indicadores gerenciais a partir das viagens e despesas cadastradas")
    void consolidaIndicadores() throws Exception {
        Long viagemAprovada1 = cadastrarViagem("Curitiba - PR");
        submeter(viagemAprovada1);
        aprovar(viagemAprovada1);

        Long viagemAprovada2 = cadastrarViagem("Curitiba - PR");
        submeter(viagemAprovada2);
        aprovar(viagemAprovada2);

        Long viagemRejeitada = cadastrarViagem("Sao Paulo - SP");
        submeter(viagemRejeitada);
        rejeitar(viagemRejeitada);

        cadastrarViagem("Foz do Iguacu - PR"); // permanece em Rascunho

        mockMvc.perform(post("/api/viagens/" + viagemAprovada1 + "/despesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"dataDespesa": "2026-09-11", "descricao": "Hotel", "valor": 300.00, "tipoDespesaId": %s}
                                """.formatted(tipoDespesaId)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/viagens/" + viagemAprovada2 + "/despesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"dataDespesa": "2026-09-11", "descricao": "Hotel", "valor": 200.00, "tipoDespesaId": %s}
                                """.formatted(tipoDespesaId)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalViagens").value(4))
                .andExpect(jsonPath("$.viagensAprovadas").value(2))
                .andExpect(jsonPath("$.viagensRejeitadas").value(1))
                .andExpect(jsonPath("$.valorTotalGasto").value(500.00))
                .andExpect(jsonPath("$.destinoMaisVisitado").value("Curitiba - PR"))
                .andExpect(jsonPath("$.custoMedioPorViagem").value(250.00));
    }

    @Test
    @DisplayName("Retorna indicadores zerados quando nao ha viagens cadastradas")
    void indicadoresZeradosSemViagens() throws Exception {
        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalViagens").value(0))
                .andExpect(jsonPath("$.viagensAprovadas").value(0))
                .andExpect(jsonPath("$.viagensRejeitadas").value(0))
                .andExpect(jsonPath("$.valorTotalGasto").value(0))
                .andExpect(jsonPath("$.destinoMaisVisitado").value(nullValue()))
                .andExpect(jsonPath("$.custoMedioPorViagem").value(0));
    }
}
