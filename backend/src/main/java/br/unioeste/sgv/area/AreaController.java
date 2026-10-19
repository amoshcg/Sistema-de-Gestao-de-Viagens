package br.unioeste.sgv.area;

import br.unioeste.sgv.area.dto.AreaRequest;
import br.unioeste.sgv.area.dto.AreaResponse;
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
@RequestMapping("/api/areas")
public class AreaController {

    private final AreaService service;

    public AreaController(AreaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AreaResponse> cadastrar(@Valid @RequestBody AreaRequest request) {
        AreaResponse area = service.cadastrar(request);
        return ResponseEntity.created(URI.create("/api/areas/" + area.id())).body(area);
    }

    @GetMapping
    public List<AreaResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public AreaResponse buscar(@PathVariable Long id) {
        return AreaResponse.de(service.buscarPorId(id));
    }
}
