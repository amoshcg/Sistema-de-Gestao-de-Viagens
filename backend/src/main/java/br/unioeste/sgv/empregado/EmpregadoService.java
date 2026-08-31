package br.unioeste.sgv.empregado;

import br.unioeste.sgv.area.Area;
import br.unioeste.sgv.area.AreaRepository;
import br.unioeste.sgv.common.ConflitoException;
import br.unioeste.sgv.common.RecursoNaoEncontradoException;
import br.unioeste.sgv.empregado.dto.EmpregadoRequest;
import br.unioeste.sgv.empregado.dto.EmpregadoResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmpregadoService {

    private final EmpregadoRepository repository;
    private final AreaRepository areaRepository;

    public EmpregadoService(EmpregadoRepository repository, AreaRepository areaRepository) {
        this.repository = repository;
        this.areaRepository = areaRepository;
    }

    @Transactional
    public EmpregadoResponse cadastrar(EmpregadoRequest request) {
        String matricula = request.matricula().trim();
        if (repository.existsByMatricula(matricula)) {
            throw new ConflitoException("Ja existe um empregado cadastrado com esta matricula");
        }
        Area area = areaRepository.findById(request.areaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Area nao encontrada"));
        Empregado empregado = new Empregado(matricula, request.nome().trim(), area);
        return EmpregadoResponse.de(repository.save(empregado));
    }

    @Transactional(readOnly = true)
    public List<EmpregadoResponse> listar() {
        return repository.findAllByOrderByNomeAsc()
                .stream()
                .map(EmpregadoResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public Empregado buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empregado nao encontrado"));
    }
}
