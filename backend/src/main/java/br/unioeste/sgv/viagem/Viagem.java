package br.unioeste.sgv.viagem;

import br.unioeste.sgv.area.Area;
import br.unioeste.sgv.cargo.Cargo;
import br.unioeste.sgv.empregado.Empregado;
import br.unioeste.sgv.meiotransporte.MeioTransporte;
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
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "viagem")
public class Viagem {

    /** Nomes de status usados para guiar as transicoes (RN-CAD-002, RN-SUB-001 etc). */
    public static final String STATUS_RASCUNHO = "Rascunho";
    public static final String STATUS_SOLICITADA = "Solicitada";
    public static final String STATUS_APROVADA = "Aprovada";
    public static final String STATUS_REJEITADA = "Rejeitada";
    public static final String STATUS_AJUSTE_SOLICITADO = "Ajuste solicitado";
    public static final String STATUS_CANCELADA = "Cancelada";

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

    /** RN-CAD-002: toda viagem nasce em Rascunho. Cadastro de Status de Viagem: modulo statusviagem. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "status_viagem_id", nullable = false)
    private StatusViagem statusViagem;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    /** Fotografia da area do empregado no momento da criacao da viagem; imutavel dai em diante. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "area_solicitante_id", nullable = false, updatable = false)
    private Area areaSolicitante;

    /** Fotografia do cargo do empregado no momento da criacao da viagem; imutavel dai em diante. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cargo_solicitante_id", nullable = false, updatable = false)
    private Cargo cargoSolicitante;

    protected Viagem() {
        // exigido pelo JPA
    }

    public Viagem(String destino, LocalDate dataSaida, LocalDate dataRetorno, String motivo,
                  MeioTransporte meioTransporte, Empregado empregado, StatusViagem statusRascunho,
                  Area areaSolicitante, Cargo cargoSolicitante) {
        this.destino = destino;
        this.dataSaida = dataSaida;
        this.dataRetorno = dataRetorno;
        this.motivo = motivo;
        this.meioTransporte = meioTransporte;
        this.empregado = empregado;
        this.statusViagem = statusRascunho;
        this.criadoEm = OffsetDateTime.now();
        this.areaSolicitante = areaSolicitante;
        this.cargoSolicitante = cargoSolicitante;
    }

    /** RF-ALT-001: reaplica RN-CAD-003/RN-CAD-004; numero, empregado e status permanecem imutaveis aqui. */
    public void atualizar(String destino, LocalDate dataSaida, LocalDate dataRetorno, String motivo,
                           MeioTransporte meioTransporte) {
        this.destino = destino;
        this.dataSaida = dataSaida;
        this.dataRetorno = dataRetorno;
        this.motivo = motivo;
        this.meioTransporte = meioTransporte;
    }

    /** RN-SUB-001: transicao de status de Rascunho (ou Ajuste solicitado) para Solicitada. */
    public void submeter(StatusViagem solicitada) {
        this.statusViagem = solicitada;
    }

    /** Encerra o fluxo cancelando a viagem, a pedido do proprio solicitante. */
    public void cancelar(StatusViagem cancelada) {
        this.statusViagem = cancelada;
    }

    /** O gestor aprova a viagem, encerrando o fluxo. */
    public void aprovar(StatusViagem aprovada) {
        this.statusViagem = aprovada;
    }

    /** O gestor rejeita a viagem, encerrando o fluxo. */
    public void rejeitar(StatusViagem rejeitada) {
        this.statusViagem = rejeitada;
    }

    /** O gestor devolve a viagem para o solicitante ajustar e reenviar. */
    public void solicitarAjuste(StatusViagem ajusteSolicitado) {
        this.statusViagem = ajusteSolicitado;
    }

    public boolean isRascunho() {
        return STATUS_RASCUNHO.equalsIgnoreCase(statusViagem.getDescricao());
    }

    /** Viagens em Rascunho ou com Ajuste solicitado podem ser editadas, canceladas ou (re)submetidas. */
    public boolean isEditavel() {
        return isRascunho() || STATUS_AJUSTE_SOLICITADO.equalsIgnoreCase(statusViagem.getDescricao());
    }

    public boolean isSolicitada() {
        return STATUS_SOLICITADA.equalsIgnoreCase(statusViagem.getDescricao());
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

    public StatusViagem getStatusViagem() {
        return statusViagem;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public Area getAreaSolicitante() {
        return areaSolicitante;
    }

    public Cargo getCargoSolicitante() {
        return cargoSolicitante;
    }
}
