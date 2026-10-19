package br.unioeste.sgv.area.dto;

import br.unioeste.sgv.area.Area;

public record AreaResponse(Long id, String nome) {

    public static AreaResponse de(Area area) {
        return new AreaResponse(area.getId(), area.getNome());
    }
}
