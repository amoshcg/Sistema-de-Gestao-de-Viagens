package br.unioeste.sgv.viagem.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Dados de entrada da alteracao de viagem (RF-ALT-001).
 * O empregado e o numero da viagem sao imutaveis (RN-CAD-001) e por isso nao aparecem aqui.
 */
public record ViagemEdicaoRequest(

        @NotBlank(message = "O destino e obrigatorio")
        @Size(max = 120, message = "O destino deve ter no maximo 120 caracteres")
        String destino,

        @NotNull(message = "A data de saida e obrigatoria")
        LocalDate dataSaida,

        @NotNull(message = "A data de retorno e obrigatoria")
        LocalDate dataRetorno,

        @NotBlank(message = "O motivo e obrigatorio")
        @Size(max = 500, message = "O motivo deve ter no maximo 500 caracteres")
        String motivo,

        @NotNull(message = "O meio de transporte e obrigatorio")
        Long meioTransporteId
) {

    /** RN-CAD-004: reaplicada a cada alteracao (RF-ALT-001). */
    @AssertTrue(message = "A data de retorno deve ser igual ou posterior a data de saida")
    public boolean isPeriodoValido() {
        if (dataSaida == null || dataRetorno == null) {
            return true;
        }
        return !dataRetorno.isBefore(dataSaida);
    }
}
