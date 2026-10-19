package br.unioeste.sgv.meiotransporte;

import br.unioeste.sgv.common.RecursoNaoEncontradoException;
import br.unioeste.sgv.meiotransporte.dto.MeioTransporteResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MeioTransporteService {

    private final MeioTransporteRepository repository;

    public MeioTransporteService(MeioTransporteRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<MeioTransporteResponse> listar() {
        return repository.findAllByOrderByDescricaoAsc()
                .stream()
                .map(MeioTransporteResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public MeioTransporte buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Meio de transporte nao encontrado"));
    }
}
