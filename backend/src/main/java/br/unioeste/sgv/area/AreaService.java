package br.unioeste.sgv.area;

import br.unioeste.sgv.area.dto.AreaRequest;
import br.unioeste.sgv.area.dto.AreaResponse;
import br.unioeste.sgv.common.ConflitoException;
import br.unioeste.sgv.common.RecursoNaoEncontradoException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AreaService {

    private final AreaRepository repository;

    public AreaService(AreaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public AreaResponse cadastrar(AreaRequest request) {
        String nome = request.nome().trim();
        if (repository.existsByNome(nome)) {
            throw new ConflitoException("Ja existe uma area cadastrada com este nome");
        }
        return AreaResponse.de(repository.save(new Area(nome)));
    }

    @Transactional(readOnly = true)
    public List<AreaResponse> listar() {
        return repository.findAllByOrderByNomeAsc()
                .stream()
                .map(AreaResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public Area buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Area nao encontrada"));
    }
}
