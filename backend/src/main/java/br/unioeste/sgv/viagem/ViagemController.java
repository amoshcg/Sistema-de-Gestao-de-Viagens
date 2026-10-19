package br.unioeste.sgv.viagem;

import br.unioeste.sgv.viagem.dto.GestorAcaoRequest;
import br.unioeste.sgv.viagem.dto.GestorJustificativaRequest;
import br.unioeste.sgv.viagem.dto.ViagemEdicaoRequest;
import br.unioeste.sgv.viagem.dto.ViagemRequest;
import br.unioeste.sgv.viagem.dto.ViagemResponse;
import br.unioeste.sgv.viagem.dto.ViagemStatusHistoricoResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    /** RF#6: consulta de viagens com filtros opcionais e combinaveis de destino, periodo e situacao. */
    @GetMapping
    public List<ViagemResponse> pesquisar(
            @RequestParam(required = false) String destino,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) String situacao) {
        return service.pesquisar(destino, dataInicio, dataFim, situacao);
    }

    /** RF#6: destinos distintos ja cadastrados, para o dropdown de pesquisa. */
    @GetMapping("/destinos")
    public List<String> destinos() {
        return service.listarDestinos();
    }

    @GetMapping("/{id}")
    public ViagemResponse buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ViagemResponse alterar(@PathVariable Long id, @Valid @RequestBody ViagemEdicaoRequest request) {
        return service.alterar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/submissao")
    public ViagemResponse submeter(@PathVariable Long id) {
        return service.submeter(id);
    }

    @PostMapping("/{id}/cancelamento")
    public ViagemResponse cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }

    @PostMapping("/{id}/aprovacao")
    public ViagemResponse aprovar(@PathVariable Long id, @Valid @RequestBody GestorAcaoRequest request) {
        return service.aprovar(id, request);
    }

    @PostMapping("/{id}/rejeicao")
    public ViagemResponse rejeitar(@PathVariable Long id, @Valid @RequestBody GestorJustificativaRequest request) {
        return service.rejeitar(id, request);
    }

    @PostMapping("/{id}/ajuste")
    public ViagemResponse solicitarAjuste(@PathVariable Long id, @Valid @RequestBody GestorJustificativaRequest request) {
        return service.solicitarAjuste(id, request);
    }

    @GetMapping("/{id}/historico")
    public List<ViagemStatusHistoricoResponse> historico(@PathVariable Long id) {
        return service.historico(id);
    }
}
