package br.unioeste.sgv.statusviagem;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Cadastro dos status possiveis de uma viagem (Rascunho, Solicitada, Aprovada, Rejeitada,
 * Ajuste solicitado, Cancelada). Mantido como tabela propria (RH: "Manter Status de Viagem"),
 * seguindo o modelo ER da equipe.
 */
@Entity
@Table(name = "status_viagem", uniqueConstraints = @UniqueConstraint(name = "uk_status_viagem_descricao", columnNames = "descricao"))
public class StatusViagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String descricao;

    protected StatusViagem() {
        // exigido pelo JPA
    }

    public StatusViagem(String descricao) {
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }
}
