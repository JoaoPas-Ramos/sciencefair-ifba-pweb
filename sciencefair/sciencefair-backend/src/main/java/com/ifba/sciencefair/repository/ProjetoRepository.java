package com.ifba.sciencefair.repository;

import com.ifba.sciencefair.entity.Projeto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjetoRepository extends JpaRepository<Projeto, Long> {
}