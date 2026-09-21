package br.unioeste.sgv.tipodespesa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Opcoes pre-definidas de tipo de despesa (Controle Financeiro, Secao 5: Hospedagem,
 * Alimentacao, Transporte, Combustivel, Pedagios, Outras despesas). A lista e mantida via
 * migracao (Flyway); nao ha cadastro pelo usuario.
 */
@Entity
@Table(name = "tipo_despesa")
public class TipoDespesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nome;

    protected TipoDespesa() {
        // exigido pelo JPA
    }

    public TipoDespesa(String nome) {
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
}
