package br.unioeste.sgv.area.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AreaRequest(

        @NotBlank(message = "O nome e obrigatorio")
        @Size(max = 45, message = "O nome deve ter no maximo 45 caracteres")
        String nome
) {
}
