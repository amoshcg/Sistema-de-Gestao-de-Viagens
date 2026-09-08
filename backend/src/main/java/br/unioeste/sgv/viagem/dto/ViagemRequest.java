package br.unioeste.sgv.viagem.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Dados de entrada do cadastro de viagem (RF-CAD-001).
 * RN-CAD-003: destino, data de saida, data de retorno, motivo e meio de transporte sao obrigatorios.
 * RN-CAD-001: o empregado responsavel vem do cadastro de Empregados.
 */
public record ViagemRequest(

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
        Long meioTransporteId,

        @NotNull(message = "O empregado e obrigatorio")
        Long empregadoId
) {

    /** RN-CAD-004: a data de retorno deve ser igual ou posterior a data de saida. */
    @AssertTrue(message = "A data de retorno deve ser igual ou posterior a data de saida")
    public boolean isPeriodoValido() {
        if (dataSaida == null || dataRetorno == null) {
            return true; // ausencia de data ja e reportada pelo @NotNull
        }
        return !dataRetorno.isBefore(dataSaida);
    }
}
