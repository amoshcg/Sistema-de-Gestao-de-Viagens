package br.unioeste.sgv.despesa.dto;

import java.math.BigDecimal;
import java.util.List;

/** Resumo financeiro consolidado da viagem: total gasto e as despesas que o compoem. */
public record ResumoFinanceiroResponse(
        Long viagemId,
        int quantidadeDespesas,
        BigDecimal valorTotal,
        List<DespesaResponse> despesas
) {
}
