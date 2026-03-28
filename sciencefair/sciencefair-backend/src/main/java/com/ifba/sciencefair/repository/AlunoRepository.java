package com.ifba.sciencefair.repository;

import com.ifba.sciencefair.entity.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
}