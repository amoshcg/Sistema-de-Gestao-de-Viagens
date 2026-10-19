package br.unioeste.sgv.despesa.dto;

import br.unioeste.sgv.despesa.Despesa;
import java.math.BigDecimal;
import java.time.LocalDate;

public record DespesaResponse(
        Long id,
        LocalDate dataDespesa,
        String descricao,
        BigDecimal valor,
        Long viagemId,
        Long tipoDespesaId,
        String tipoDespesaNome
) {

    public static DespesaResponse de(Despesa despesa) {
        return new DespesaResponse(
                despesa.getId(),
                despesa.getDataDespesa(),
                despesa.getDescricao(),
                despesa.getValor(),
                despesa.getViagem().getId(),
                despesa.getTipoDespesa().getId(),
                despesa.getTipoDespesa().getNome()
        );
    }
}
