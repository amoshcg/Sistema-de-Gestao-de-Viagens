package br.unioeste.sgv.cargo.dto;

import br.unioeste.sgv.cargo.Cargo;

public record CargoResponse(Long id, String nome) {

    public static CargoResponse de(Cargo cargo) {
        return new CargoResponse(cargo.getId(), cargo.getNome());
    }
}
