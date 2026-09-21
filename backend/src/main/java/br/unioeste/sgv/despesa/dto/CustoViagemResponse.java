package br.unioeste.sgv.despesa.dto;

import java.math.BigDecimal;

/**
 * Custos da viagem por categoria (Sprint final): considera somente deslocamento,
 * hospedagem e taxi. Categorias de despesa fora desse recorte (ex.: Alimentacao,
 * Outras despesas) nao entram nesse calculo nem no total aqui apresentado.
 */
public record CustoViagemResponse(
        Long viagemId,
        BigDecimal custoDeslocamento,
        BigDecimal custoHospedagem,
        BigDecimal custoTaxi,
        BigDecimal custoTotal
) {
}
