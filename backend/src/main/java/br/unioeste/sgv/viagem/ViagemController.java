package br.unioeste.sgv.viagem;

import br.unioeste.sgv.viagem.dto.ViagemRequest;
import br.unioeste.sgv.viagem.dto.ViagemResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/viagens")
public class ViagemController {

    private final ViagemService service;

    public ViagemController(ViagemService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ViagemResponse> cadastrar(@Valid @RequestBody ViagemRequest request) {
        ViagemResponse viagem = service.cadastrar(request);
        return ResponseEntity.created(URI.create("/api/viagens/" + viagem.id())).body(viagem);
    }

    @GetMapping
    public List<ViagemResponse> listar() {
        return service.listar();
    }
}
