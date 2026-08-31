package br.unioeste.sgv.viagem.dto;

import br.unioeste.sgv.empregado.Empregado;
import br.unioeste.sgv.meiotransporte.MeioTransporte;
import br.unioeste.sgv.viagem.Viagem;
import java.time.LocalDate;

/**
 * Dados devolvidos na consulta de viagens (RN-CON-002: destino, periodo, situacao e responsavel;
 * RF-CON-001: dados completos, incluindo o numero da viagem e os dados do empregado).
 */
public record ViagemResponse(
        Long id,
        Long numero,
        String destino,
        LocalDate dataSaida,
        LocalDate dataRetorno,
        String motivo,
        Long meioTransporteId,
        String meioTransporteDescricao,
        String situacao,
        String situacaoDescricao,
        Long empregadoId,
        String empregadoMatricula,
        String empregadoNome,
        Long empregadoAreaId,
        String empregadoAreaNome
) {

    public static ViagemResponse de(Viagem viagem) {
        Empregado empregado = viagem.getEmpregado();
        MeioTransporte meioTransporte = viagem.getMeioTransporte();
        return new ViagemResponse(
                viagem.getId(),
                viagem.getNumero(),
                viagem.getDestino(),
                viagem.getDataSaida(),
                viagem.getDataRetorno(),
                viagem.getMotivo(),
                meioTransporte.getId(),
                meioTransporte.getDescricao(),
                viagem.getSituacao().name(),
                viagem.getSituacao().getDescricao(),
                empregado.getId(),
                empregado.getMatricula(),
                empregado.getNome(),
                empregado.getArea().getId(),
                empregado.getArea().getNome()
        );
    }
}
