package br.unioeste.sgv.cargo;

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
class CargoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CargoRepository repository;

    @BeforeEach
    void limparBase() {
        repository.deleteAll();
    }

    private String json(String nome) {
        return """
                {"nome": "%s"}
                """.formatted(nome);
    }

    @Test
    @DisplayName("Cadastra um cargo valido")
    void cadastraCargoValido() throws Exception {
        mockMvc.perform(post("/api/cargos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Colaborador")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Colaborador"));
    }

    @Test
    @DisplayName("Nome obrigatorio ausente retorna 400")
    void rejeitaNomeAusente() throws Exception {
        mockMvc.perform(post("/api/cargos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.nome").exists());
    }

    @Test
    @DisplayName("Nome duplicado retorna 409")
    void rejeitaNomeDuplicado() throws Exception {
        mockMvc.perform(post("/api/cargos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Gestor")))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/cargos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Gestor")))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Lista os cargos cadastrados em ordem alfabetica")
    void listaCargos() throws Exception {
        mockMvc.perform(post("/api/cargos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Gestor")))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/cargos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Colaborador")))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/cargos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Colaborador"))
                .andExpect(jsonPath("$[1].nome").value("Gestor"));
    }
}
