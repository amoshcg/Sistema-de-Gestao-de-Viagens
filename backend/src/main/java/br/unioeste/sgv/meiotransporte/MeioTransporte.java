package br.unioeste.sgv.meiotransporte;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Opcoes pre-definidas de meio de transporte (RF-CAD-001, observacao).
 * A lista e mantida via migracao (Flyway); nao ha cadastro pelo usuario.
 */
@Entity
@Table(name = "meio_transporte")
public class MeioTransporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String descricao;

    protected MeioTransporte() {
        // exigido pelo JPA
    }

    public MeioTransporte(String descricao) {
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }
}
