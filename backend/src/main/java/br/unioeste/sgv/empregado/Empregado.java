package br.unioeste.sgv.empregado;

import br.unioeste.sgv.area.Area;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Cadastro de Empregados: fonte de onde a viagem obtem o nome do responsavel.
 */
@Entity
@Table(name = "empregado", uniqueConstraints = @UniqueConstraint(name = "uk_empregado_matricula", columnNames = "matricula"))
public class Empregado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String matricula;

    @Column(nullable = false, length = 120)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;

    protected Empregado() {
        // exigido pelo JPA
    }

    public Empregado(String matricula, String nome, Area area) {
        this.matricula = matricula;
        this.nome = nome;
        this.area = area;
    }

    public Long getId() {
        return id;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getNome() {
        return nome;
    }

    public Area getArea() {
        return area;
    }
}
