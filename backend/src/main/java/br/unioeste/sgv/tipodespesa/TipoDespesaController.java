package br.unioeste.sgv.tipodespesa;

import br.unioeste.sgv.tipodespesa.dto.TipoDespesaResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tipos-despesa")
public class TipoDespesaController {

    private final TipoDespesaService service;

    public TipoDespesaController(TipoDespesaService service) {
        this.service = service;
    }

    @GetMapping
    public List<TipoDespesaResponse> listar() {
        return service.listar();
    }
}
