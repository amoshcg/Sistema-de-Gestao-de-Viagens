package br.unioeste.sgv.viagem;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ViagemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ViagemRepository repository;

    @BeforeEach
    void limparBase() {
        repository.deleteAll();
    }

    private String json(String destino, String saida, String retorno, String motivo,
                        String meio, String responsavel) {
        return """
                {
                  "destino": "%s",
                  "dataSaida": "%s",
                  "dataRetorno": "%s",
                  "motivo": "%s",
                  "meioTransporte": "%s",
                  "responsavel": "%s"
                }
                """.formatted(destino, saida, retorno, motivo, meio, responsavel);
    }

    @Test
    @DisplayName("RF-CAD-001 / RN-CAD-002: viagem valida e criada na situacao Rascunho")
    void cadastraViagemValida() throws Exception {
        mockMvc.perform(post("/api/viagens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Curitiba", "2026-09-10", "2026-09-12",
                                "Reuniao com cliente", "AEREO", "Carlos Penteado")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.destino").value("Curitiba"))
                .andExpect(jsonPath("$.situacao").value("RASCUNHO"))
                .andExpect(jsonPath("$.responsavel").value("Carlos Penteado"));
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
                .andExpect(jsonPath("$.erros.meioTransporte").exists())
                .andExpect(jsonPath("$.erros.responsavel").exists());
    }

    @Test
    @DisplayName("RN-CAD-004: data de retorno anterior a de saida retorna 400")
    void rejeitaPeriodoInvertido() throws Exception {
        mockMvc.perform(post("/api/viagens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Cascavel", "2026-09-20", "2026-09-18",
                                "Treinamento", "RODOVIARIO", "Carlos Penteado")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.periodoValido").exists());
    }

    @Test
    @DisplayName("RN-CAD-004: retorno no mesmo dia da saida e aceito")
    void aceitaPeriodoDeUmDia() throws Exception {
        mockMvc.perform(post("/api/viagens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Toledo", "2026-09-20", "2026-09-20",
                                "Visita tecnica", "VEICULO_PROPRIO", "Carlos Penteado")))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("RF-CON-002: listagem retorna as viagens mais recentes primeiro")
    void listaViagensMaisRecentesPrimeiro() throws Exception {
        mockMvc.perform(post("/api/viagens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Foz do Iguacu", "2026-09-01", "2026-09-03",
                                "Congresso", "RODOVIARIO", "Carlos Penteado")))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/viagens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Sao Paulo", "2026-10-05", "2026-10-08",
                                "Evento", "AEREO", "Carlos Penteado")))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/viagens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].destino").value("Sao Paulo"))
                .andExpect(jsonPath("$[1].destino").value("Foz do Iguacu"));
    }
}
