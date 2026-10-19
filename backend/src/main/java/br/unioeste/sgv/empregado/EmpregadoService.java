package br.unioeste.sgv.empregado;

import br.unioeste.sgv.area.Area;
import br.unioeste.sgv.area.AreaRepository;
import br.unioeste.sgv.cargo.Cargo;
import br.unioeste.sgv.cargo.CargoRepository;
import br.unioeste.sgv.common.ConflitoException;
import br.unioeste.sgv.common.RecursoNaoEncontradoException;
import br.unioeste.sgv.empregado.dto.EmpregadoEdicaoRequest;
import br.unioeste.sgv.empregado.dto.EmpregadoRequest;
import br.unioeste.sgv.empregado.dto.EmpregadoResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmpregadoService {

    private final EmpregadoRepository repository;
    private final AreaRepository areaRepository;
    private final CargoRepository cargoRepository;

    public EmpregadoService(EmpregadoRepository repository, AreaRepository areaRepository,
                             CargoRepository cargoRepository) {
        this.repository = repository;
        this.areaRepository = areaRepository;
        this.cargoRepository = cargoRepository;
    }

    @Transactional
    public EmpregadoResponse cadastrar(EmpregadoRequest request) {
        String matricula = request.matricula().trim();
        if (repository.existsByMatricula(matricula)) {
            throw new ConflitoException("Ja existe um empregado cadastrado com esta matricula");
        }
        Area area = areaRepository.findById(request.areaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Area nao encontrada"));
        Cargo cargo = cargoRepository.findById(request.cargoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cargo nao encontrado"));
        Empregado empregado = new Empregado(matricula, request.nome().trim(), area, cargo);
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

    /** Permite corrigir o nome e atualizar area/cargo do empregado ao longo do tempo. */
    @Transactional
    public EmpregadoResponse alterar(Long id, EmpregadoEdicaoRequest request) {
        Empregado empregado = buscarPorId(id);
        Area area = areaRepository.findById(request.areaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Area nao encontrada"));
        Cargo cargo = cargoRepository.findById(request.cargoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cargo nao encontrado"));
        empregado.atualizar(request.nome().trim(), area, cargo);
        return EmpregadoResponse.de(empregado);
    }
}
