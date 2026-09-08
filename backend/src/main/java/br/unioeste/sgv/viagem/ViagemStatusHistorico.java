package br.unioeste.sgv.viagem;

import br.unioeste.sgv.empregado.Empregado;
import br.unioeste.sgv.statusviagem.StatusViagem;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

/** Historico das mudancas de status de uma viagem: data, responsavel e status resultante. */
@Entity
@Table(name = "viagem_status_historico")
public class ViagemStatusHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "viagem_id", nullable = false, updatable = false)
    private Viagem viagem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "status_viagem_id", nullable = false, updatable = false)
    private StatusViagem statusViagem;

    @Column(name = "data_mudanca", nullable = false, updatable = false)
    private OffsetDateTime dataMudanca = OffsetDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "responsavel_id", nullable = false, updatable = false)
    private Empregado responsavel;

    @Column(length = 500, updatable = false)
    private String justificativa;

    protected ViagemStatusHistorico() {
        // exigido pelo JPA
    }

    public ViagemStatusHistorico(Viagem viagem, StatusViagem statusViagem, Empregado responsavel,
                                  String justificativa) {
        this.viagem = viagem;
        this.statusViagem = statusViagem;
        this.responsavel = responsavel;
        this.justificativa = justificativa;
        this.dataMudanca = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Viagem getViagem() {
        return viagem;
    }

    public StatusViagem getStatusViagem() {
        return statusViagem;
    }

    public OffsetDateTime getDataMudanca() {
        return dataMudanca;
    }

    public Empregado getResponsavel() {
        return responsavel;
    }

    public String getJustificativa() {
        return justificativa;
    }
}
