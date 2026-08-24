package br.unioeste.sgv.viagem;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "viagem")
public class Viagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String destino;

    @Column(name = "data_saida", nullable = false)
    private LocalDate dataSaida;

    @Column(name = "data_retorno", nullable = false)
    private LocalDate dataRetorno;

    @Column(nullable = false, length = 500)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "meio_transporte", nullable = false, length = 30)
    private MeioTransporte meioTransporte;

    /** RN-CAD-001: vinculo definido na criacao e imutavel. */
    @Column(nullable = false, length = 120, updatable = false)
    private String responsavel;

    /** RN-CAD-002: toda viagem nasce em RASCUNHO. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SituacaoViagem situacao = SituacaoViagem.RASCUNHO;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    protected Viagem() {
        // exigido pelo JPA
    }

    public Viagem(String destino, LocalDate dataSaida, LocalDate dataRetorno, String motivo,
                  MeioTransporte meioTransporte, String responsavel) {
        this.destino = destino;
        this.dataSaida = dataSaida;
        this.dataRetorno = dataRetorno;
        this.motivo = motivo;
        this.meioTransporte = meioTransporte;
        this.responsavel = responsavel;
        this.situacao = SituacaoViagem.RASCUNHO;
        this.criadoEm = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getDestino() {
        return destino;
    }

    public LocalDate getDataSaida() {
        return dataSaida;
    }

    public LocalDate getDataRetorno() {
        return dataRetorno;
    }

    public String getMotivo() {
        return motivo;
    }

    public MeioTransporte getMeioTransporte() {
        return meioTransporte;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public SituacaoViagem getSituacao() {
        return situacao;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }
}
