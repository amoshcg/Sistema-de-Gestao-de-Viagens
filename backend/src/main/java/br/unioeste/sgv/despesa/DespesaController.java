package br.unioeste.sgv.despesa;

import br.unioeste.sgv.despesa.dto.DespesaRequest;
import br.unioeste.sgv.despesa.dto.DespesaResponse;
import br.unioeste.sgv.despesa.dto.ResumoFinanceiroResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/viagens/{viagemId}/despesas")
public class DespesaController {

    private final DespesaService service;

    public DespesaController(DespesaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DespesaResponse> cadastrar(@PathVariable Long viagemId,
                                                       @Valid @RequestBody DespesaRequest request) {
        DespesaResponse despesa = service.cadastrar(viagemId, request);
        return ResponseEntity.created(URI.create("/api/viagens/" + viagemId + "/despesas/" + despesa.id()))
                .body(despesa);
    }

    @GetMapping
    public List<DespesaResponse> listar(@PathVariable Long viagemId) {
        return service.listarPorViagem(viagemId);
    }

    @GetMapping("/resumo")
    public ResumoFinanceiroResponse resumo(@PathVariable Long viagemId) {
        return service.resumoFinanceiro(viagemId);
    }
}
