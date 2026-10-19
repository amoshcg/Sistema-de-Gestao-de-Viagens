package br.unioeste.sgv.despesa.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record DespesaRequest(

        @NotNull(message = "A data da despesa e obrigatoria")
        @PastOrPresent(message = "A data da despesa nao pode ser futura")
        LocalDate dataDespesa,

        @NotBlank(message = "A descricao e obrigatoria")
        @Size(max = 255, message = "A descricao deve ter no maximo 255 caracteres")
        String descricao,

        @NotNull(message = "O valor e obrigatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "O valor deve ser maior que zero")
        BigDecimal valor,

        @NotNull(message = "O tipo de despesa e obrigatorio")
        Long tipoDespesaId
) {
}
