package com.ifba.sciencefair.entity;

import jakarta.persistence.*;
import com.ifba.sciencefair.entity.Professor;

@Entity
@Table(name = "projetos")
public class Projeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private String areaTematica;

    @Column(nullable = false)
    private String status;

   @ManyToOne
@JoinColumn(name = "feira_id", nullable = false)
private Feira feira;

@ManyToOne
@JoinColumn(name = "professor_id", nullable = false)
private Professor professor;
    
    public Projeto() {
    }

    public Projeto(Long id, String titulo, String descricao, String areaTematica, String status, Feira feira) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.areaTematica = areaTematica;
        this.status = status;
        this.feira = feira;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getAreaTematica() {
        return areaTematica;
    }

    public String getStatus() {
        return status;
    }

    public Feira getFeira() {
        return feira;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setAreaTematica(String areaTematica) {
        this.areaTematica = areaTematica;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFeira(Feira feira) {
        this.feira = feira;
    }
    public Professor getProfessor() {
    return professor;
}

public void setProfessor(Professor professor) {
    this.professor = professor;
}
}