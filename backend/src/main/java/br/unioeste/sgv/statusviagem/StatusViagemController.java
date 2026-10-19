package br.unioeste.sgv.statusviagem;

import br.unioeste.sgv.statusviagem.dto.StatusViagemRequest;
import br.unioeste.sgv.statusviagem.dto.StatusViagemResponse;
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
@RequestMapping("/api/status-viagem")
public class StatusViagemController {

    private final StatusViagemService service;

    public StatusViagemController(StatusViagemService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<StatusViagemResponse> cadastrar(@Valid @RequestBody StatusViagemRequest request) {
        StatusViagemResponse status = service.cadastrar(request);
        return ResponseEntity.created(URI.create("/api/status-viagem/" + status.id())).body(status);
    }

    @GetMapping
    public List<StatusViagemResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public StatusViagemResponse buscar(@PathVariable Long id) {
        return StatusViagemResponse.de(service.buscarPorId(id));
    }
}
