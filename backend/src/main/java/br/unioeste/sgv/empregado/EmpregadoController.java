package br.unioeste.sgv.empregado;

import br.unioeste.sgv.empregado.dto.EmpregadoRequest;
import br.unioeste.sgv.empregado.dto.EmpregadoResponse;
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
@RequestMapping("/api/empregados")
public class EmpregadoController {

    private final EmpregadoService service;

    public EmpregadoController(EmpregadoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EmpregadoResponse> cadastrar(@Valid @RequestBody EmpregadoRequest request) {
        EmpregadoResponse empregado = service.cadastrar(request);
        return ResponseEntity.created(URI.create("/api/empregados/" + empregado.id())).body(empregado);
    }

    @GetMapping
    public List<EmpregadoResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public EmpregadoResponse buscar(@PathVariable Long id) {
        return EmpregadoResponse.de(service.buscarPorId(id));
    }
}
