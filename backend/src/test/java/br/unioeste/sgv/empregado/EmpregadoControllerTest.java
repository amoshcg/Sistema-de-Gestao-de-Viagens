package br.unioeste.sgv.empregado;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.unioeste.sgv.area.Area;
import br.unioeste.sgv.area.AreaRepository;
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
class EmpregadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmpregadoRepository repository;

    @Autowired
    private AreaRepository areaRepository;

    private Long areaId;

    @BeforeEach
    void limparBase() {
        repository.deleteAll();
        areaRepository.deleteAll();
        areaId = areaRepository.save(new Area("Financeiro")).getId();
    }

    private String json(String matricula, String nome, Long areaId) {
        return """
                {"matricula": "%s", "nome": "%s", "areaId": %s}
                """.formatted(matricula, nome, areaId);
    }

    @Test
    @DisplayName("Cadastra um empregado valido")
    void cadastraEmpregadoValido() throws Exception {
        mockMvc.perform(post("/api/empregados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("E100", "Ana Souza", areaId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.matricula").value("E100"))
                .andExpect(jsonPath("$.nome").value("Ana Souza"))
                .andExpect(jsonPath("$.areaId").value(areaId))
                .andExpect(jsonPath("$.areaNome").value("Financeiro"));
    }

    @Test
    @DisplayName("Campos obrigatorios ausentes retornam 400")
    void rejeitaCamposObrigatorios() throws Exception {
        mockMvc.perform(post("/api/empregados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"matricula": "", "nome": ""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.matricula").exists())
                .andExpect(jsonPath("$.erros.nome").exists())
                .andExpect(jsonPath("$.erros.areaId").exists());
    }

    @Test
    @DisplayName("Area inexistente retorna 404")
    void rejeitaAreaInexistente() throws Exception {
        mockMvc.perform(post("/api/empregados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("E150", "Bruno Melo", 999999L)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Matricula duplicada retorna 409")
    void rejeitaMatriculaDuplicada() throws Exception {
        mockMvc.perform(post("/api/empregados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("E200", "Joao Lima", areaId)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/empregados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("E200", "Joao Lima Junior", areaId)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Lista os empregados cadastrados em ordem alfabetica")
    void listaEmpregados() throws Exception {
        mockMvc.perform(post("/api/empregados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("E300", "Zeca Alves", areaId)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/empregados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("E301", "Ana Beatriz", areaId)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/empregados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Ana Beatriz"))
                .andExpect(jsonPath("$[1].nome").value("Zeca Alves"));
    }
}
