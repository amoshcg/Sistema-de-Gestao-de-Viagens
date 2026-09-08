package br.unioeste.sgv.cargo;

import br.unioeste.sgv.cargo.dto.CargoRequest;
import br.unioeste.sgv.cargo.dto.CargoResponse;
import br.unioeste.sgv.common.ConflitoException;
import br.unioeste.sgv.common.RecursoNaoEncontradoException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CargoService {

    private final CargoRepository repository;

    public CargoService(CargoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CargoResponse cadastrar(CargoRequest request) {
        String nome = request.nome().trim();
        if (repository.existsByNome(nome)) {
            throw new ConflitoException("Ja existe um cargo cadastrado com este nome");
        }
        return CargoResponse.de(repository.save(new Cargo(nome)));
    }

    @Transactional(readOnly = true)
    public List<CargoResponse> listar() {
        return repository.findAllByOrderByNomeAsc()
                .stream()
                .map(CargoResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public Cargo buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cargo nao encontrado"));
    }
}
