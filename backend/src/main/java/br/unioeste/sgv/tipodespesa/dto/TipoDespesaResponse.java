package br.unioeste.sgv.tipodespesa.dto;

import br.unioeste.sgv.tipodespesa.TipoDespesa;

public record TipoDespesaResponse(Long id, String nome) {

    public static TipoDespesaResponse de(TipoDespesa tipoDespesa) {
        return new TipoDespesaResponse(tipoDespesa.getId(), tipoDespesa.getNome());
    }
}
