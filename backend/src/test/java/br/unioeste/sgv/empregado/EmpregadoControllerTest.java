package br.unioeste.sgv.empregado;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.unioeste.sgv.area.Area;
import br.unioeste.sgv.area.AreaRepository;
import br.unioeste.sgv.cargo.Cargo;
import br.unioeste.sgv.cargo.CargoRepository;
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

    @Autowired
    private CargoRepository cargoRepository;

    private Long areaId;
    private Long cargoId;

    @BeforeEach
    void limparBase() {
        repository.deleteAll();
        areaRepository.deleteAll();
        cargoRepository.deleteAll();
        areaId = areaRepository.save(new Area("Financeiro")).getId();
        cargoId = cargoRepository.save(new Cargo("Colaborador")).getId();
    }

    private String json(String matricula, String nome, Long areaId, Long cargoId) {
        return """
                {"matricula": "%s", "nome": "%s", "areaId": %s, "cargoId": %s}
                """.formatted(matricula, nome, areaId, cargoId);
    }

    @Test
    @DisplayName("Cadastra um empregado valido")
    void cadastraEmpregadoValido() throws Exception {
        mockMvc.perform(post("/api/empregados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("0100-1", "Ana Souza", areaId, cargoId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.matricula").value("0100-1"))
                .andExpect(jsonPath("$.nome").value("Ana Souza"))
                .andExpect(jsonPath("$.areaId").value(areaId))
                .andExpect(jsonPath("$.areaNome").value("Financeiro"))
                .andExpect(jsonPath("$.cargoId").value(cargoId))
                .andExpect(jsonPath("$.cargoNome").value("Colaborador"));
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
                .andExpect(jsonPath("$.erros.areaId").exists())
                .andExpect(jsonPath("$.erros.cargoId").exists());
    }

    @Test
    @DisplayName("Matricula fora do formato XXXX-X retorna 400")
    void rejeitaMatriculaForaDoFormato() throws Exception {
        mockMvc.perform(post("/api/empregados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("E150", "Bruno Melo", areaId, cargoId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.matricula").exists());
    }

    @Test
    @DisplayName("Area inexistente retorna 404")
    void rejeitaAreaInexistente() throws Exception {
        mockMvc.perform(post("/api/empregados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("0150-1", "Bruno Melo", 999999L, cargoId)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Cargo inexistente retorna 404")
    void rejeitaCargoInexistente() throws Exception {
        mockMvc.perform(post("/api/empregados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("0151-1", "Bruno Melo", areaId, 999999L)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Matricula duplicada retorna 409")
    void rejeitaMatriculaDuplicada() throws Exception {
        mockMvc.perform(post("/api/empregados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("0200-2", "Joao Lima", areaId, cargoId)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/empregados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("0200-2", "Joao Lima Junior", areaId, cargoId)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Lista os empregados cadastrados em ordem alfabetica")
    void listaEmpregados() throws Exception {
        mockMvc.perform(post("/api/empregados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("0300-3", "Zeca Alves", areaId, cargoId)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/empregados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("0301-3", "Ana Beatriz", areaId, cargoId)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/empregados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Ana Beatriz"))
                .andExpect(jsonPath("$[1].nome").value("Zeca Alves"));
    }

    @Test
    @DisplayName("Altera nome, area e cargo de um empregado existente, mantendo a matricula")
    void alteraEmpregadoExistente() throws Exception {
        Long gestorId = cargoRepository.save(new Cargo("Gestor")).getId();
        Long outraAreaId = areaRepository.save(new Area("Recursos Humanos")).getId();

        String corpo = mockMvc.perform(post("/api/empregados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("0400-4", "Willian Francisco", areaId, cargoId)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long id = repository.findAll().stream()
                .filter(e -> e.getMatricula().equals("0400-4"))
                .findFirst().orElseThrow().getId();

        mockMvc.perform(put("/api/empregados/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "Willian Francisco da Silva", "areaId": %s, "cargoId": %s}
                                """.formatted(outraAreaId, gestorId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricula").value("0400-4"))
                .andExpect(jsonPath("$.nome").value("Willian Francisco da Silva"))
                .andExpect(jsonPath("$.areaId").value(outraAreaId))
                .andExpect(jsonPath("$.cargoId").value(gestorId));
    }
}
