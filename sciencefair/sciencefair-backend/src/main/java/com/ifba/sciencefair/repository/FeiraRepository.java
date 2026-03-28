package com.ifba.sciencefair.repository;

import com.ifba.sciencefair.entity.Feira;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeiraRepository extends JpaRepository<Feira, Long> {
}