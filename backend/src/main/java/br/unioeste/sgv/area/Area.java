package br.unioeste.sgv.area;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/** Cadastro de Areas: usado para classificar os Empregados. */
@Entity
@Table(name = "area", uniqueConstraints = @UniqueConstraint(name = "uk_area_nome", columnNames = "nome"))
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 45)
    private String nome;

    protected Area() {
        // exigido pelo JPA
    }

    public Area(String nome) {
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
}
