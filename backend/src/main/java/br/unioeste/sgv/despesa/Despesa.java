package br.unioeste.sgv.despesa;

import br.unioeste.sgv.tipodespesa.TipoDespesa;
import br.unioeste.sgv.viagem.Viagem;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/** Despesa lancada pelo colaborador em uma viagem Aprovada (Controle Financeiro, Secao 5). */
@Entity
@Table(name = "despesa")
public class Despesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_despesa", nullable = false)
    private LocalDate dataDespesa;

    @Column(nullable = false, length = 255)
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "viagem_id", nullable = false, updatable = false)
    private Viagem viagem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_despesa_id", nullable = false)
    private TipoDespesa tipoDespesa;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    protected Despesa() {
        // exigido pelo JPA
    }

    public Despesa(LocalDate dataDespesa, String descricao, BigDecimal valor, Viagem viagem, TipoDespesa tipoDespesa) {
        this.dataDespesa = dataDespesa;
        this.descricao = descricao;
        this.valor = valor;
        this.viagem = viagem;
        this.tipoDespesa = tipoDespesa;
        this.criadoEm = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDataDespesa() {
        return dataDespesa;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public Viagem getViagem() {
        return viagem;
    }

    public TipoDespesa getTipoDespesa() {
        return tipoDespesa;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }
}
