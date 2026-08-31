package br.unioeste.sgv.meiotransporte;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class MeioTransporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MeioTransporteRepository repository;

    @BeforeEach
    void limparBase() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Lista as opcoes de meio de transporte em ordem alfabetica")
    void listaMeiosTransporte() throws Exception {
        repository.save(new MeioTransporte("Rodoviario"));
        repository.save(new MeioTransporte("Aereo"));

        mockMvc.perform(get("/api/meios-transporte"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].descricao").value("Aereo"))
                .andExpect(jsonPath("$[1].descricao").value("Rodoviario"));
    }
}
