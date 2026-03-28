package com.ifba.sciencefair.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "professores")
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String departamento;

    @Column(nullable = false, unique = true)
    private String matricula;

    public Professor() {
    }

    public Professor(Long id, String nome, String email, String departamento, String matricula) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.departamento = departamento;
        this.matricula = matricula;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getDepartamento() {
        return departamento;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
}