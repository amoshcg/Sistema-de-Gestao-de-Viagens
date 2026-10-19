package br.unioeste.sgv.tipodespesa;

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
class TipoDespesaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TipoDespesaRepository repository;

    @BeforeEach
    void limparBase() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Lista os tipos de despesa em ordem alfabetica")
    void listaTiposDespesa() throws Exception {
        repository.save(new TipoDespesa("Transporte"));
        repository.save(new TipoDespesa("Alimentação"));

        mockMvc.perform(get("/api/tipos-despesa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Alimentação"))
                .andExpect(jsonPath("$[1].nome").value("Transporte"));
    }
}
