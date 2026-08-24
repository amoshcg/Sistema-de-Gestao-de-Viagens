package br.unioeste.sgv.viagem.dto;

import br.unioeste.sgv.viagem.Viagem;
import java.time.LocalDate;

/**
 * Dados devolvidos na consulta de viagens (RN-CON-002: destino, periodo, situacao e responsavel).
 */
public record ViagemResponse(
        Long id,
        String destino,
        LocalDate dataSaida,
        LocalDate dataRetorno,
        String motivo,
        String meioTransporte,
        String meioTransporteDescricao,
        String situacao,
        String situacaoDescricao,
        String responsavel
) {

    public static ViagemResponse de(Viagem viagem) {
        return new ViagemResponse(
                viagem.getId(),
                viagem.getDestino(),
                viagem.getDataSaida(),
                viagem.getDataRetorno(),
                viagem.getMotivo(),
                viagem.getMeioTransporte().name(),
                viagem.getMeioTransporte().getDescricao(),
                viagem.getSituacao().name(),
                viagem.getSituacao().getDescricao(),
                viagem.getResponsavel()
        );
    }
}
