package br.unioeste.sgv.dashboard.dto;

import java.math.BigDecimal;

/** Indicadores gerenciais consolidados (RF#7 / Sprint final: dashboard). */
public record DashboardResponse(
        long totalViagens,
        long viagensAprovadas,
        long viagensRejeitadas,
        BigDecimal valorTotalGasto,
        String destinoMaisVisitado,
        BigDecimal custoMedioPorViagem
) {
}
