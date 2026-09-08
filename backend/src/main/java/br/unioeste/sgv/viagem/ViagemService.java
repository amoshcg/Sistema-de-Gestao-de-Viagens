package br.unioeste.sgv.viagem;

import br.unioeste.sgv.common.ConflitoException;
import br.unioeste.sgv.common.RecursoNaoEncontradoException;
import br.unioeste.sgv.empregado.Empregado;
import br.unioeste.sgv.empregado.EmpregadoRepository;
import br.unioeste.sgv.meiotransporte.MeioTransporte;
import br.unioeste.sgv.meiotransporte.MeioTransporteRepository;
import br.unioeste.sgv.statusviagem.StatusViagem;
import br.unioeste.sgv.statusviagem.StatusViagemRepository;
import br.unioeste.sgv.viagem.dto.GestorAcaoRequest;
import br.unioeste.sgv.viagem.dto.GestorJustificativaRequest;
import br.unioeste.sgv.viagem.dto.ViagemEdicaoRequest;
import br.unioeste.sgv.viagem.dto.ViagemRequest;
import br.unioeste.sgv.viagem.dto.ViagemResponse;
import br.unioeste.sgv.viagem.dto.ViagemStatusHistoricoResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ViagemService {

    private final ViagemRepository repository;
    private final EmpregadoRepository empregadoRepository;
    private final MeioTransporteRepository meioTransporteRepository;
    private final StatusViagemRepository statusViagemRepository;
    private final ViagemStatusHistoricoRepository historicoRepository;

    public ViagemService(ViagemRepository repository, EmpregadoRepository empregadoRepository,
                          MeioTransporteRepository meioTransporteRepository,
                          StatusViagemRepository statusViagemRepository,
                          ViagemStatusHistoricoRepository historicoRepository) {
        this.repository = repository;
        this.empregadoRepository = empregadoRepository;
        this.meioTransporteRepository = meioTransporteRepository;
        this.statusViagemRepository = statusViagemRepository;
        this.historicoRepository = historicoRepository;
    }

    /** RF-CAD-001: cadastra a viagem sempre na situacao Rascunho, vinculada a um empregado cadastrado. */
    @Transactional
    public ViagemResponse cadastrar(ViagemRequest request) {
        Empregado empregado = empregadoRepository.findById(request.empregadoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empregado nao encontrado"));
        MeioTransporte meioTransporte = meioTransporteRepository.findById(request.meioTransporteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Meio de transporte nao encontrado"));
        StatusViagem rascunho = buscarStatus(Viagem.STATUS_RASCUNHO);
        Viagem viagem = new Viagem(
                request.destino().trim(),
                request.dataSaida(),
                request.dataRetorno(),
                request.motivo().trim(),
                meioTransporte,
                empregado,
                rascunho,
                empregado.getArea(),
                empregado.getCargo()
        );
        viagem = repository.save(viagem);
        registrarHistorico(viagem, rascunho, empregado, null);
        return ViagemResponse.de(viagem);
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

    /** RF-ALT-001 / RN-ALT-001: so e permitido alterar viagens em Rascunho ou com Ajuste solicitado. */
    @Transactional
    public ViagemResponse alterar(Long id, ViagemEdicaoRequest request) {
        Viagem viagem = buscarEntidade(id);
        if (!viagem.isEditavel()) {
            throw new ConflitoException("Somente viagens em Rascunho ou com ajuste solicitado podem ser alteradas");
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
        historicoRepository.deleteAll(historicoRepository.findAllByViagemIdOrderByDataMudancaAscIdAsc(id));
        repository.delete(viagem);
    }

    /**
     * RF-SUB-001 / RN-SUB-001: submete a viagem para analise, levando-a de Rascunho (ou Ajuste
     * solicitado) para Solicitada. Tambem usado para reenviar apos um pedido de ajuste do gestor.
     */
    @Transactional
    public ViagemResponse submeter(Long id) {
        Viagem viagem = buscarEntidade(id);
        if (!viagem.isEditavel()) {
            throw new ConflitoException("Somente viagens em Rascunho ou com ajuste solicitado podem ser submetidas para analise");
        }
        StatusViagem solicitada = buscarStatus(Viagem.STATUS_SOLICITADA);
        viagem.submeter(solicitada);
        registrarHistorico(viagem, solicitada, viagem.getEmpregado(), null);
        return ViagemResponse.de(viagem);
    }

    /** O solicitante desiste da viagem, encerrando o fluxo. */
    @Transactional
    public ViagemResponse cancelar(Long id) {
        Viagem viagem = buscarEntidade(id);
        if (!viagem.isEditavel()) {
            throw new ConflitoException("Somente viagens em Rascunho ou com ajuste solicitado podem ser canceladas");
        }
        StatusViagem cancelada = buscarStatus(Viagem.STATUS_CANCELADA);
        viagem.cancelar(cancelada);
        registrarHistorico(viagem, cancelada, viagem.getEmpregado(), null);
        return ViagemResponse.de(viagem);
    }

    /** O gestor aprova a viagem Solicitada, encerrando o fluxo. */
    @Transactional
    public ViagemResponse aprovar(Long id, GestorAcaoRequest request) {
        Viagem viagem = buscarEntidade(id);
        if (!viagem.isSolicitada()) {
            throw new ConflitoException("Somente viagens Solicitadas podem ser aprovadas");
        }
        Empregado gestor = validarGestor(request.gestorId());
        StatusViagem aprovada = buscarStatus(Viagem.STATUS_APROVADA);
        viagem.aprovar(aprovada);
        registrarHistorico(viagem, aprovada, gestor, null);
        return ViagemResponse.de(viagem);
    }

    /** O gestor rejeita a viagem Solicitada, encerrando o fluxo. */
    @Transactional
    public ViagemResponse rejeitar(Long id, GestorJustificativaRequest request) {
        Viagem viagem = buscarEntidade(id);
        if (!viagem.isSolicitada()) {
            throw new ConflitoException("Somente viagens Solicitadas podem ser rejeitadas");
        }
        Empregado gestor = validarGestor(request.gestorId());
        StatusViagem rejeitada = buscarStatus(Viagem.STATUS_REJEITADA);
        viagem.rejeitar(rejeitada);
        registrarHistorico(viagem, rejeitada, gestor, request.justificativa().trim());
        return ViagemResponse.de(viagem);
    }

    /** O gestor devolve a viagem Solicitada para o solicitante ajustar e reenviar. */
    @Transactional
    public ViagemResponse solicitarAjuste(Long id, GestorJustificativaRequest request) {
        Viagem viagem = buscarEntidade(id);
        if (!viagem.isSolicitada()) {
            throw new ConflitoException("Somente viagens Solicitadas podem receber pedido de ajuste");
        }
        Empregado gestor = validarGestor(request.gestorId());
        StatusViagem ajusteSolicitado = buscarStatus(Viagem.STATUS_AJUSTE_SOLICITADO);
        viagem.solicitarAjuste(ajusteSolicitado);
        registrarHistorico(viagem, ajusteSolicitado, gestor, request.justificativa().trim());
        return ViagemResponse.de(viagem);
    }

    /** Historico de mudancas de situacao da viagem, da mais antiga para a mais recente. */
    @Transactional(readOnly = true)
    public List<ViagemStatusHistoricoResponse> historico(Long id) {
        buscarEntidade(id);
        return historicoRepository.findAllByViagemIdOrderByDataMudancaAscIdAsc(id)
                .stream()
                .map(ViagemStatusHistoricoResponse::de)
                .toList();
    }

    private Empregado validarGestor(Long gestorId) {
        Empregado gestor = empregadoRepository.findById(gestorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Gestor nao encontrado"));
        if (!"Gestor".equalsIgnoreCase(gestor.getCargo().getNome())) {
            throw new ConflitoException("Somente empregados com cargo Gestor podem analisar viagens");
        }
        return gestor;
    }

    private StatusViagem buscarStatus(String descricao) {
        return statusViagemRepository.findByDescricaoIgnoreCase(descricao)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Status de viagem '" + descricao + "' nao encontrado"));
    }

    private void registrarHistorico(Viagem viagem, StatusViagem statusViagem, Empregado responsavel,
                                     String justificativa) {
        historicoRepository.save(new ViagemStatusHistorico(viagem, statusViagem, responsavel, justificativa));
    }

    private Viagem buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Viagem nao encontrada"));
    }
}
