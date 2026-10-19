package br.unioeste.sgv.viagem.dto;

import br.unioeste.sgv.empregado.Empregado;
import br.unioeste.sgv.viagem.ViagemStatusHistorico;
import java.time.OffsetDateTime;

/** Uma linha do historico de mudancas de situacao de uma viagem. */
public record ViagemStatusHistoricoResponse(
        Long id,
        Long situacaoId,
        String situacaoDescricao,
        OffsetDateTime dataMudanca,
        Long responsavelId,
        String responsavelNome,
        String justificativa
) {

    public static ViagemStatusHistoricoResponse de(ViagemStatusHistorico historico) {
        Empregado responsavel = historico.getResponsavel();
        return new ViagemStatusHistoricoResponse(
                historico.getId(),
                historico.getStatusViagem().getId(),
                historico.getStatusViagem().getDescricao(),
                historico.getDataMudanca(),
                responsavel.getId(),
                responsavel.getNome(),
                historico.getJustificativa()
        );
    }
}
