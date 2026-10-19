package br.unioeste.sgv.viagem.dto;

import jakarta.validation.constraints.NotNull;

/** Dados de entrada para uma acao de gestor que nao exige justificativa (aprovacao). */
public record GestorAcaoRequest(

        @NotNull(message = "O gestor e obrigatorio")
        Long gestorId
) {
}
