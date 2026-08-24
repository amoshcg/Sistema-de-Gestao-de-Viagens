package br.unioeste.sgv.viagem;

import br.unioeste.sgv.viagem.dto.ViagemRequest;
import br.unioeste.sgv.viagem.dto.ViagemResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ViagemService {

    private final ViagemRepository repository;

    public ViagemService(ViagemRepository repository) {
        this.repository = repository;
    }

    /** RF-CAD-001: cadastra a viagem sempre na situacao RASCUNHO. */
    @Transactional
    public ViagemResponse cadastrar(ViagemRequest request) {
        Viagem viagem = new Viagem(
                request.destino().trim(),
                request.dataSaida(),
                request.dataRetorno(),
                request.motivo().trim(),
                request.meioTransporte(),
                request.responsavel().trim()
        );
        return ViagemResponse.de(repository.save(viagem));
    }

    /** RF-CON-002: lista as viagens cadastradas, das mais recentes para as mais antigas. */
    @Transactional(readOnly = true)
    public List<ViagemResponse> listar() {
        return repository.findAllByOrderByCriadoEmDescIdDesc()
                .stream()
                .map(ViagemResponse::de)
                .toList();
    }
}
