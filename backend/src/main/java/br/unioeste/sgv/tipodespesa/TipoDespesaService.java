package br.unioeste.sgv.tipodespesa;

import br.unioeste.sgv.common.RecursoNaoEncontradoException;
import br.unioeste.sgv.tipodespesa.dto.TipoDespesaResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TipoDespesaService {

    private final TipoDespesaRepository repository;

    public TipoDespesaService(TipoDespesaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TipoDespesaResponse> listar() {
        return repository.findAllByOrderByNomeAsc()
                .stream()
                .map(TipoDespesaResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public TipoDespesa buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tipo de despesa nao encontrado"));
    }
}
