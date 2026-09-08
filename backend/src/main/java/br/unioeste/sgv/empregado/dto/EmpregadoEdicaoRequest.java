package br.unioeste.sgv.empregado.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Dados de entrada da alteracao de empregado.
 * A matricula e imutavel apos o cadastro e por isso nao aparece aqui.
 */
public record EmpregadoEdicaoRequest(

        @NotBlank(message = "O nome e obrigatorio")
        @Size(max = 120, message = "O nome deve ter no maximo 120 caracteres")
        String nome,

        @NotNull(message = "A area e obrigatoria")
        Long areaId,

        @NotNull(message = "O cargo e obrigatorio")
        Long cargoId
) {
}
