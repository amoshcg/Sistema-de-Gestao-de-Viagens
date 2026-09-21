package br.unioeste.sgv.cargo;

import br.unioeste.sgv.cargo.dto.CargoRequest;
import br.unioeste.sgv.cargo.dto.CargoResponse;
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
@RequestMapping("/api/cargos")
public class CargoController {

    private final CargoService service;

    public CargoController(CargoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CargoResponse> cadastrar(@Valid @RequestBody CargoRequest request) {
        CargoResponse cargo = service.cadastrar(request);
        return ResponseEntity.created(URI.create("/api/cargos/" + cargo.id())).body(cargo);
    }

    @GetMapping
    public List<CargoResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public CargoResponse buscar(@PathVariable Long id) {
        return CargoResponse.de(service.buscarPorId(id));
    }
}
