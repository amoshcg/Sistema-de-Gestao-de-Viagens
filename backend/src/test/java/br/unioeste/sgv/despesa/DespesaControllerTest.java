package br.unioeste.sgv.despesa;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import br.unioeste.sgv.tipodespesa.TipoDespesa;
import br.unioeste.sgv.tipodespesa.TipoDespesaRepository;
import br.unioeste.sgv.viagem.ViagemRepository;
import br.unioeste.sgv.viagem.ViagemStatusHistoricoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
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
class DespesaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DespesaRepository repository;

    @Autowired
    private ViagemRepository viagemRepository;

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

    @Autowired
    private TipoDespesaRepository tipoDespesaRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Long empregadoId;
    private Long gestorId;
    private Long meioTransporteId;
    private Long tipoDespesaId;

    @BeforeEach
    void limparBase() throws Exception {
        repository.deleteAll();
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

    private Long cadastrarViagem() throws Exception {
        String corpo = """
                {
                  "destino": "Curitiba - PR",
                  "dataSaida": "2026-09-10",
                  "dataRetorno": "2026-09-12",
                  "motivo": "Reuniao com cliente",
                  "meioTransporteId": %s,
                  "empregadoId": %s
                }
                """.formatted(meioTransporteId, empregadoId);
        MvcResult resultado = mockMvc.perform(post("/api/viagens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode json = objectMapper.readTree(resultado.getResponse().getContentAsString());
        return json.get("id").asLong();
    }

    private Long cadastrarViagemAprovada() throws Exception {
        Long viagemId = cadastrarViagem();
        mockMvc.perform(post("/api/viagens/" + viagemId + "/submissao")).andExpect(status().isOk());
        mockMvc.perform(post("/api/viagens/" + viagemId + "/aprovacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"gestorId": %s}
                                """.formatted(gestorId)))
                .andExpect(status().isOk());
        return viagemId;
    }

    private String jsonDespesa(String data, String descricao, String valor, Long tipoDespesaId) {
        return """
                {
                  "dataDespesa": "%s",
                  "descricao": "%s",
                  "valor": %s,
                  "tipoDespesaId": %s
                }
                """.formatted(data, descricao, valor, tipoDespesaId);
    }

    @Test
    @DisplayName("Registra uma despesa valida em uma viagem Aprovada")
    void registraDespesaValida() throws Exception {
        Long viagemId = cadastrarViagemAprovada();

        mockMvc.perform(post("/api/viagens/" + viagemId + "/despesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDespesa("2026-09-11", "Hotel Centro", "350.00", tipoDespesaId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.descricao").value("Hotel Centro"))
                .andExpect(jsonPath("$.valor").value(350.00))
                .andExpect(jsonPath("$.tipoDespesaNome").value("Hospedagem"))
                .andExpect(jsonPath("$.viagemId").value(viagemId));
    }

    @Test
    @DisplayName("Rejeita despesa em viagem que ainda nao foi aprovada")
    void rejeitaDespesaEmViagemNaoAprovada() throws Exception {
        Long viagemId = cadastrarViagem(); // permanece em Rascunho

        mockMvc.perform(post("/api/viagens/" + viagemId + "/despesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDespesa("2026-09-11", "Hotel Centro", "350.00", tipoDespesaId)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Rejeita despesa em viagem rejeitada")
    void rejeitaDespesaEmViagemRejeitada() throws Exception {
        Long viagemId = cadastrarViagem();
        mockMvc.perform(post("/api/viagens/" + viagemId + "/submissao")).andExpect(status().isOk());
        mockMvc.perform(post("/api/viagens/" + viagemId + "/rejeicao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"gestorId": %s, "justificativa": "Sem orcamento"}
                                """.formatted(gestorId)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/viagens/" + viagemId + "/despesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDespesa("2026-09-11", "Hotel Centro", "350.00", tipoDespesaId)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Rejeita valor igual ou inferior a zero")
    void rejeitaValorNaoPositivo() throws Exception {
        Long viagemId = cadastrarViagemAprovada();

        mockMvc.perform(post("/api/viagens/" + viagemId + "/despesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDespesa("2026-09-11", "Hotel Centro", "0", tipoDespesaId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.valor").exists());
    }

    @Test
    @DisplayName("Rejeita data futura")
    void rejeitaDataFutura() throws Exception {
        Long viagemId = cadastrarViagemAprovada();
        String dataFutura = LocalDate.now().plusDays(1).toString();

        mockMvc.perform(post("/api/viagens/" + viagemId + "/despesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDespesa(dataFutura, "Hotel Centro", "100.00", tipoDespesaId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.dataDespesa").exists());
    }

    @Test
    @DisplayName("Tipo de despesa inexistente retorna 404")
    void rejeitaTipoDespesaInexistente() throws Exception {
        Long viagemId = cadastrarViagemAprovada();

        mockMvc.perform(post("/api/viagens/" + viagemId + "/despesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDespesa("2026-09-11", "Hotel Centro", "100.00", 9999L)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Viagem inexistente retorna 404")
    void rejeitaViagemInexistente() throws Exception {
        mockMvc.perform(post("/api/viagens/9999/despesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDespesa("2026-09-11", "Hotel Centro", "100.00", tipoDespesaId)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Lista as despesas da viagem e consolida o resumo financeiro")
    void listaDespesasEResumoFinanceiro() throws Exception {
        Long viagemId = cadastrarViagemAprovada();
        Long tipoAlimentacao = tipoDespesaRepository.save(new TipoDespesa("Alimentação")).getId();

        mockMvc.perform(post("/api/viagens/" + viagemId + "/despesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDespesa("2026-09-10", "Hotel Centro", "350.00", tipoDespesaId)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/viagens/" + viagemId + "/despesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDespesa("2026-09-11", "Almoço", "45.50", tipoAlimentacao)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/viagens/" + viagemId + "/despesas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].descricao").value("Hotel Centro"))
                .andExpect(jsonPath("$[1].descricao").value("Almoço"));

        mockMvc.perform(get("/api/viagens/" + viagemId + "/despesas/resumo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidadeDespesas").value(2))
                .andExpect(jsonPath("$.valorTotal").value(395.50))
                .andExpect(jsonPath("$.despesas.length()").value(2));
    }

    @Test
    @DisplayName("Calcula os custos da viagem por categoria (deslocamento, hospedagem e taxi)")
    void calculaCustosPorCategoria() throws Exception {
        Long viagemId = cadastrarViagemAprovada();
        Long tipoTransporte = tipoDespesaRepository.save(new TipoDespesa("Transporte")).getId();
        Long tipoTaxi = tipoDespesaRepository.save(new TipoDespesa("Táxi")).getId();
        Long tipoAlimentacao = tipoDespesaRepository.save(new TipoDespesa("Alimentação")).getId();

        mockMvc.perform(post("/api/viagens/" + viagemId + "/despesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDespesa("2026-09-10", "Hotel Centro", "300.00", tipoDespesaId)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/viagens/" + viagemId + "/despesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDespesa("2026-09-10", "Passagem aerea", "500.00", tipoTransporte)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/viagens/" + viagemId + "/despesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDespesa("2026-09-11", "Corrida ate o hotel", "40.00", tipoTaxi)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/viagens/" + viagemId + "/despesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDespesa("2026-09-11", "Almoço", "45.50", tipoAlimentacao)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/viagens/" + viagemId + "/despesas/custos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.viagemId").value(viagemId))
                .andExpect(jsonPath("$.custoDeslocamento").value(500.00))
                .andExpect(jsonPath("$.custoHospedagem").value(300.00))
                .andExpect(jsonPath("$.custoTaxi").value(40.00))
                .andExpect(jsonPath("$.custoTotal").value(840.00));
    }
}
