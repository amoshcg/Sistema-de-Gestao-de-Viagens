package br.unioeste.sgv.viagem;

/**
 * Situacoes previstas no ciclo de vida da viagem (Especificacao, secao 4).
 * O modulo de Planejamento cria a viagem sempre em RASCUNHO (RN-CAD-002).
 */
public enum SituacaoViagem {
    RASCUNHO("Rascunho"),
    SOLICITADA("Solicitada"),
    APROVADA("Aprovada"),
    REJEITADA("Rejeitada");

    private final String descricao;

    SituacaoViagem(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
