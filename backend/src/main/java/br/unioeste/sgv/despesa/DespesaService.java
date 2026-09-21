package br.unioeste.sgv.despesa;

import br.unioeste.sgv.common.ConflitoException;
import br.unioeste.sgv.common.RecursoNaoEncontradoException;
import br.unioeste.sgv.despesa.dto.CustoViagemResponse;
import br.unioeste.sgv.despesa.dto.DespesaRequest;
import br.unioeste.sgv.despesa.dto.DespesaResponse;
import br.unioeste.sgv.despesa.dto.ResumoFinanceiroResponse;
import br.unioeste.sgv.tipodespesa.TipoDespesa;
import br.unioeste.sgv.tipodespesa.TipoDespesaRepository;
import br.unioeste.sgv.viagem.Viagem;
import br.unioeste.sgv.viagem.ViagemRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DespesaService {

    /** Sprint final: "custo de deslocamento" agrupa as categorias ligadas a se locomover ate o destino. */
    private static final List<String> CATEGORIAS_DESLOCAMENTO = List.of("Transporte", "Combustível", "Pedágios");
    private static final List<String> CATEGORIA_HOSPEDAGEM = List.of("Hospedagem");
    private static final List<String> CATEGORIA_TAXI = List.of("Táxi");

    private final DespesaRepository repository;
    private final ViagemRepository viagemRepository;
    private final TipoDespesaRepository tipoDespesaRepository;

    public DespesaService(DespesaRepository repository, ViagemRepository viagemRepository,
                           TipoDespesaRepository tipoDespesaRepository) {
        this.repository = repository;
        this.viagemRepository = viagemRepository;
        this.tipoDespesaRepository = tipoDespesaRepository;
    }

    /**
     * Registra uma despesa da viagem. So e permitido enquanto a viagem estiver Aprovada
     * (consequentemente, viagens Rejeitadas ou em qualquer outra situacao nao podem receber
     * lancamentos). Valor <= 0 e datas futuras sao recusados pela validacao do DespesaRequest.
     */
    @Transactional
    public DespesaResponse cadastrar(Long viagemId, DespesaRequest request) {
        Viagem viagem = buscarViagem(viagemId);
        if (!viagem.isAprovada()) {
            throw new ConflitoException("Somente viagens Aprovadas podem receber lancamentos de despesas");
        }
        TipoDespesa tipoDespesa = tipoDespesaRepository.findById(request.tipoDespesaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tipo de despesa nao encontrado"));
        Despesa despesa = new Despesa(
                request.dataDespesa(),
                request.descricao().trim(),
                request.valor(),
                viagem,
                tipoDespesa
        );
        return DespesaResponse.de(repository.save(despesa));
    }

    /** Lista as despesas lancadas na viagem, da mais antiga para a mais recente. */
    @Transactional(readOnly = true)
    public List<DespesaResponse> listarPorViagem(Long viagemId) {
        buscarViagem(viagemId);
        return repository.findAllByViagemIdOrderByDataDespesaAscIdAsc(viagemId)
                .stream()
                .map(DespesaResponse::de)
                .toList();
    }

    /** Resumo financeiro consolidado: valor total gasto (calculado automaticamente) e as despesas. */
    @Transactional(readOnly = true)
    public ResumoFinanceiroResponse resumoFinanceiro(Long viagemId) {
        buscarViagem(viagemId);
        List<DespesaResponse> despesas = repository.findAllByViagemIdOrderByDataDespesaAscIdAsc(viagemId)
                .stream()
                .map(DespesaResponse::de)
                .toList();
        BigDecimal valorTotal = repository.somarValorPorViagem(viagemId);
        return new ResumoFinanceiroResponse(viagemId, despesas.size(), valorTotal, despesas);
    }

    /**
     * Custos da viagem por categoria (Sprint final: RF de calculo de custos). Considera
     * somente deslocamento, hospedagem e taxi; o total e a soma dessas tres categorias.
     */
    @Transactional(readOnly = true)
    public CustoViagemResponse calcularCustos(Long viagemId) {
        buscarViagem(viagemId);
        BigDecimal custoDeslocamento = repository.somarValorPorViagemETipos(viagemId, CATEGORIAS_DESLOCAMENTO);
        BigDecimal custoHospedagem = repository.somarValorPorViagemETipos(viagemId, CATEGORIA_HOSPEDAGEM);
        BigDecimal custoTaxi = repository.somarValorPorViagemETipos(viagemId, CATEGORIA_TAXI);
        BigDecimal custoTotal = custoDeslocamento.add(custoHospedagem).add(custoTaxi);
        return new CustoViagemResponse(viagemId, custoDeslocamento, custoHospedagem, custoTaxi, custoTotal);
    }

    private Viagem buscarViagem(Long viagemId) {
        return viagemRepository.findById(viagemId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Viagem nao encontrada"));
    }
}
