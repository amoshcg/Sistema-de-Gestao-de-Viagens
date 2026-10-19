package br.unioeste.sgv.cargo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CargoRequest(

        @NotBlank(message = "O nome e obrigatorio")
        @Size(max = 45, message = "O nome deve ter no maximo 45 caracteres")
        String nome
) {
}
