package com.ifba.sciencefair.repository;

import com.ifba.sciencefair.entity.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
}