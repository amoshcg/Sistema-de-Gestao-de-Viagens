package br.unioeste.sgv.viagem;

/**
 * Lista de opcoes pre-definidas para o campo "meio de transporte" (RF-CAD-001).
 */
public enum MeioTransporte {
    AEREO("Aereo"),
    RODOVIARIO("Rodoviario"),
    VEICULO_PROPRIO("Veiculo proprio");

    private final String descricao;

    MeioTransporte(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
