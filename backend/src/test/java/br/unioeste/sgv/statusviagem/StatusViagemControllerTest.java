package br.unioeste.sgv.statusviagem;

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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class StatusViagemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StatusViagemRepository repository;

    @BeforeEach
    void limparBase() {
        repository.deleteAll();
    }

    private String json(String descricao) {
        return """
                {"descricao": "%s"}
                """.formatted(descricao);
    }

    @Test
    @DisplayName("Cadastra um status de viagem valido")
    void cadastraStatusValido() throws Exception {
        mockMvc.perform(post("/api/status-viagem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Rascunho")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.descricao").value("Rascunho"));
    }

    @Test
    @DisplayName("Descricao obrigatoria ausente retorna 400")
    void rejeitaDescricaoAusente() throws Exception {
        mockMvc.perform(post("/api/status-viagem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.descricao").exists());
    }

    @Test
    @DisplayName("Descricao duplicada retorna 409")
    void rejeitaDescricaoDuplicada() throws Exception {
        mockMvc.perform(post("/api/status-viagem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Solicitada")))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/status-viagem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Solicitada")))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Lista os status cadastrados na ordem de criacao")
    void listaStatus() throws Exception {
        mockMvc.perform(post("/api/status-viagem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Rascunho")))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/status-viagem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Solicitada")))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/status-viagem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].descricao").value("Rascunho"))
                .andExpect(jsonPath("$[1].descricao").value("Solicitada"));
    }
}
