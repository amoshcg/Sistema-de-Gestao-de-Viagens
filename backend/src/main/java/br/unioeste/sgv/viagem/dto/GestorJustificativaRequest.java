package br.unioeste.sgv.viagem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Dados de entrada para uma acao de gestor que exige justificativa (rejeicao ou pedido de ajuste). */
public record GestorJustificativaRequest(

        @NotNull(message = "O gestor e obrigatorio")
        Long gestorId,

        @NotBlank(message = "A justificativa e obrigatoria")
        @Size(max = 500, message = "A justificativa deve ter no maximo 500 caracteres")
        String justificativa
) {
}
