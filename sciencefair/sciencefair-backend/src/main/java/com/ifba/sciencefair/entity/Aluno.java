package com.ifba.sciencefair.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "alunos")
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String matricula;

    @Column(nullable = false)
    private String turma;

    @ManyToOne
    @JoinColumn(name = "projeto_id", nullable = false)
    private Projeto projeto;

    public Aluno() {
    }

    public Aluno(Long id, String nome, String email, String matricula, String turma, Projeto projeto) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.matricula = matricula;
        this.turma = turma;
        this.projeto = projeto;
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

    public String getMatricula() {
        return matricula;
    }

    public String getTurma() {
        return turma;
    }

    public Projeto getProjeto() {
        return projeto;
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

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public void setTurma(String turma) {
        this.turma = turma;
    }

    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
    }
}