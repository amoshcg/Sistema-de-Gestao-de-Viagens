package br.unioeste.sgv.statusviagem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StatusViagemRequest(

        @NotBlank(message = "A descricao e obrigatoria")
        @Size(max = 50, message = "A descricao deve ter no maximo 50 caracteres")
        String descricao
) {
}
