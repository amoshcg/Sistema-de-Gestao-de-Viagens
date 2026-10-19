package br.unioeste.sgv.meiotransporte;

import br.unioeste.sgv.meiotransporte.dto.MeioTransporteResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/meios-transporte")
public class MeioTransporteController {

    private final MeioTransporteService service;

    public MeioTransporteController(MeioTransporteService service) {
        this.service = service;
    }

    @GetMapping
    public List<MeioTransporteResponse> listar() {
        return service.listar();
    }
}
