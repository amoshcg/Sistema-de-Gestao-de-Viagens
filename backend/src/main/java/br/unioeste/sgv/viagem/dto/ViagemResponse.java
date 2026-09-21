package br.unioeste.sgv.viagem.dto;

import br.unioeste.sgv.empregado.Empregado;
import br.unioeste.sgv.meiotransporte.MeioTransporte;
import br.unioeste.sgv.viagem.Viagem;
import java.math.BigDecimal;
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
        Long situacaoId,
        String situacaoDescricao,
        Long empregadoId,
        String empregadoMatricula,
        String empregadoNome,
        Long empregadoAreaId,
        String empregadoAreaNome,
        Long areaSolicitanteId,
        String areaSolicitanteNome,
        Long cargoSolicitanteId,
        String cargoSolicitanteNome,
        BigDecimal valorGasto
) {

    public static ViagemResponse de(Viagem viagem) {
        return de(viagem, null);
    }

    /** RF#6: a pesquisa de viagens tambem informa o gasto de cada uma (valorGasto). */
    public static ViagemResponse de(Viagem viagem, BigDecimal valorGasto) {
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
                viagem.getStatusViagem().getId(),
                viagem.getStatusViagem().getDescricao(),
                empregado.getId(),
                empregado.getMatricula(),
                empregado.getNome(),
                empregado.getArea().getId(),
                empregado.getArea().getNome(),
                viagem.getAreaSolicitante().getId(),
                viagem.getAreaSolicitante().getNome(),
                viagem.getCargoSolicitante().getId(),
                viagem.getCargoSolicitante().getNome(),
                valorGasto
        );
    }
}
