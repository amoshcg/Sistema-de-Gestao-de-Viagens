package br.unioeste.sgv.statusviagem;

import br.unioeste.sgv.common.ConflitoException;
import br.unioeste.sgv.common.RecursoNaoEncontradoException;
import br.unioeste.sgv.statusviagem.dto.StatusViagemRequest;
import br.unioeste.sgv.statusviagem.dto.StatusViagemResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StatusViagemService {

    private final StatusViagemRepository repository;

    public StatusViagemService(StatusViagemRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public StatusViagemResponse cadastrar(StatusViagemRequest request) {
        String descricao = request.descricao().trim();
        if (repository.existsByDescricao(descricao)) {
            throw new ConflitoException("Ja existe um status de viagem cadastrado com esta descricao");
        }
        return StatusViagemResponse.de(repository.save(new StatusViagem(descricao)));
    }

    @Transactional(readOnly = true)
    public List<StatusViagemResponse> listar() {
        return repository.findAllByOrderByIdAsc()
                .stream()
                .map(StatusViagemResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public StatusViagem buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Status de viagem nao encontrado"));
    }
}
