package br.unioeste.sgv.viagem;

import br.unioeste.sgv.common.ConflitoException;
import br.unioeste.sgv.common.RecursoNaoEncontradoException;
import br.unioeste.sgv.empregado.Empregado;
import br.unioeste.sgv.empregado.EmpregadoRepository;
import br.unioeste.sgv.meiotransporte.MeioTransporte;
import br.unioeste.sgv.meiotransporte.MeioTransporteRepository;
import br.unioeste.sgv.viagem.dto.ViagemEdicaoRequest;
import br.unioeste.sgv.viagem.dto.ViagemRequest;
import br.unioeste.sgv.viagem.dto.ViagemResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ViagemService {

    private final ViagemRepository repository;
    private final EmpregadoRepository empregadoRepository;
    private final MeioTransporteRepository meioTransporteRepository;

    public ViagemService(ViagemRepository repository, EmpregadoRepository empregadoRepository,
                          MeioTransporteRepository meioTransporteRepository) {
        this.repository = repository;
        this.empregadoRepository = empregadoRepository;
        this.meioTransporteRepository = meioTransporteRepository;
    }

    /** RF-CAD-001: cadastra a viagem sempre na situacao RASCUNHO, vinculada a um empregado cadastrado. */
    @Transactional
    public ViagemResponse cadastrar(ViagemRequest request) {
        Empregado empregado = empregadoRepository.findById(request.empregadoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empregado nao encontrado"));
        MeioTransporte meioTransporte = meioTransporteRepository.findById(request.meioTransporteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Meio de transporte nao encontrado"));
        Viagem viagem = new Viagem(
                request.destino().trim(),
                request.dataSaida(),
                request.dataRetorno(),
                request.motivo().trim(),
                meioTransporte,
                empregado
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

    /** RF-CON-001: dados completos de uma viagem especifica, em qualquer situacao. */
    @Transactional(readOnly = true)
    public ViagemResponse buscarPorId(Long id) {
        return ViagemResponse.de(buscarEntidade(id));
    }

    /** RF-ALT-001 / RN-ALT-001: so e permitido alterar viagens em Rascunho. */
    @Transactional
    public ViagemResponse alterar(Long id, ViagemEdicaoRequest request) {
        Viagem viagem = buscarEntidade(id);
        if (!viagem.isRascunho()) {
            throw new ConflitoException("Somente viagens em Rascunho podem ser alteradas");
        }
        MeioTransporte meioTransporte = meioTransporteRepository.findById(request.meioTransporteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Meio de transporte nao encontrado"));
        viagem.atualizar(
                request.destino().trim(),
                request.dataSaida(),
                request.dataRetorno(),
                request.motivo().trim(),
                meioTransporte
        );
        return ViagemResponse.de(viagem);
    }

    /** RF-ALT-002 / RN-ALT-001 / RN-ALT-003: exclusao definitiva, somente permitida em Rascunho. */
    @Transactional
    public void excluir(Long id) {
        Viagem viagem = buscarEntidade(id);
        if (!viagem.isRascunho()) {
            throw new ConflitoException("Somente viagens em Rascunho podem ser excluidas");
        }
        repository.delete(viagem);
    }

    /**
     * RF-SUB-001 / RN-SUB-001: submete a viagem para analise, levando-a de Rascunho para Solicitada.
     * RN-SUB-002 (completude) ja e garantida pela validacao obrigatoria no cadastro/alteracao.
     */
    @Transactional
    public ViagemResponse submeter(Long id) {
        Viagem viagem = buscarEntidade(id);
        if (!viagem.isRascunho()) {
            throw new ConflitoException("Somente viagens em Rascunho podem ser submetidas para analise");
        }
        viagem.submeter();
        return ViagemResponse.de(viagem);
    }

    private Viagem buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Viagem nao encontrada"));
    }
}
