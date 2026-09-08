package br.unioeste.sgv.statusviagem.dto;

import br.unioeste.sgv.statusviagem.StatusViagem;

public record StatusViagemResponse(Long id, String descricao) {

    public static StatusViagemResponse de(StatusViagem statusViagem) {
        return new StatusViagemResponse(statusViagem.getId(), statusViagem.getDescricao());
    }
}
