package br.unioeste.sgv.viagem;

import br.unioeste.sgv.empregado.Empregado;
import br.unioeste.sgv.meiotransporte.MeioTransporte;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "viagem")
public class Viagem {

    /** O identificador tecnico tambem serve como "numero da viagem": sequencial, unico e imutavel. */
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

    /** RF-CAD-001 (observacao): opcoes pre-definidas de meio de transporte. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meio_transporte_id", nullable = false)
    private MeioTransporte meioTransporte;

    /** RN-CAD-001: o empregado responsavel vem do cadastro de Empregados e o vinculo e imutavel apos a criacao. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empregado_id", nullable = false, updatable = false)
    private Empregado empregado;

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
                  MeioTransporte meioTransporte, Empregado empregado) {
        this.destino = destino;
        this.dataSaida = dataSaida;
        this.dataRetorno = dataRetorno;
        this.motivo = motivo;
        this.meioTransporte = meioTransporte;
        this.empregado = empregado;
        this.situacao = SituacaoViagem.RASCUNHO;
        this.criadoEm = OffsetDateTime.now();
    }

    /** RF-ALT-001: reaplica RN-CAD-003/RN-CAD-004; numero, empregado e situacao permanecem imutaveis aqui. */
    public void atualizar(String destino, LocalDate dataSaida, LocalDate dataRetorno, String motivo,
                           MeioTransporte meioTransporte) {
        this.destino = destino;
        this.dataSaida = dataSaida;
        this.dataRetorno = dataRetorno;
        this.motivo = motivo;
        this.meioTransporte = meioTransporte;
    }

    /** RN-SUB-001: transicao de situacao de Rascunho para Solicitada. */
    public void submeter() {
        this.situacao = SituacaoViagem.SOLICITADA;
    }

    public boolean isRascunho() {
        return this.situacao == SituacaoViagem.RASCUNHO;
    }

    public Long getId() {
        return id;
    }

    public Long getNumero() {
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

    public Empregado getEmpregado() {
        return empregado;
    }

    public SituacaoViagem getSituacao() {
        return situacao;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }
}
