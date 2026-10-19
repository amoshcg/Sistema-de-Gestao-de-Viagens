package br.unioeste.sgv.cargo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/** Cadastro de Cargos: usado para classificar os Empregados (ex: Colaborador, Gestor). */
@Entity
@Table(name = "cargo", uniqueConstraints = @UniqueConstraint(name = "uk_cargo_nome", columnNames = "nome"))
public class Cargo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 45)
    private String nome;

    protected Cargo() {
        // exigido pelo JPA
    }

    public Cargo(String nome) {
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
}
