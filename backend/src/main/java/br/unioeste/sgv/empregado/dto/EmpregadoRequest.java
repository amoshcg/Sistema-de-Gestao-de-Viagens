package br.unioeste.sgv.empregado.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Dados de entrada do cadastro de empregado (matricula, nome, area, cargo). */
public record EmpregadoRequest(

        @NotBlank(message = "A matricula e obrigatoria")
        @Size(max = 20, message = "A matricula deve ter no maximo 20 caracteres")
        @Pattern(regexp = "^[0-9]{4}-[0-9]$", message = "A matricula deve estar no formato XXXX-X (apenas digitos)")
        String matricula,

        @NotBlank(message = "O nome e obrigatorio")
        @Size(max = 120, message = "O nome deve ter no maximo 120 caracteres")
        String nome,

        @NotNull(message = "A area e obrigatoria")
        Long areaId,

        @NotNull(message = "O cargo e obrigatorio")
        Long cargoId
) {
}
