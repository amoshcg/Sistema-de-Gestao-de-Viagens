package br.unioeste.sgv.dashboard;

import br.unioeste.sgv.dashboard.dto.DashboardResponse;
import br.unioeste.sgv.despesa.DespesaRepository;
import br.unioeste.sgv.viagem.Viagem;
import br.unioeste.sgv.viagem.ViagemRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

    private final ViagemRepository viagemRepository;
    private final DespesaRepository despesaRepository;

    public DashboardService(ViagemRepository viagemRepository, DespesaRepository despesaRepository) {
        this.viagemRepository = viagemRepository;
        this.despesaRepository = despesaRepository;
    }

    /** RF#7: quantidade de viagens (total/aprovadas/rejeitadas), gasto total, destino mais visitado e custo medio. */
    @Transactional(readOnly = true)
    public DashboardResponse indicadores() {
        List<Viagem> viagens = viagemRepository.findAll();

        long totalViagens = viagens.size();
        long viagensAprovadas = viagens.stream().filter(Viagem::isAprovada).count();
        long viagensRejeitadas = viagens.stream().filter(Viagem::isRejeitada).count();

        String destinoMaisVisitado = viagens.stream()
                .collect(Collectors.groupingBy(Viagem::getDestino, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        BigDecimal valorTotalGasto = despesaRepository.somarValorTotal();
        long viagensComDespesa = despesaRepository.contarViagensComDespesa();
        BigDecimal custoMedioPorViagem = viagensComDespesa == 0
                ? BigDecimal.ZERO
                : valorTotalGasto.divide(BigDecimal.valueOf(viagensComDespesa), 2, RoundingMode.HALF_UP);

        return new DashboardResponse(
                totalViagens,
                viagensAprovadas,
                viagensRejeitadas,
                valorTotalGasto,
                destinoMaisVisitado,
                custoMedioPorViagem
        );
    }
}
