package br.unioeste.sgv.meiotransporte.dto;

import br.unioeste.sgv.meiotransporte.MeioTransporte;

public record MeioTransporteResponse(Long id, String descricao) {

    public static MeioTransporteResponse de(MeioTransporte meioTransporte) {
        return new MeioTransporteResponse(meioTransporte.getId(), meioTransporte.getDescricao());
    }
}
