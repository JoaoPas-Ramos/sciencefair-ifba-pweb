package com.ifba.sciencefair.service;

import com.ifba.sciencefair.entity.Professor;
import com.ifba.sciencefair.repository.ProfessorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;

    public ProfessorService(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    public List<Professor> listarTodos() {
        return professorRepository.findAll();
    }

    public Professor buscarPorId(Long id) {
        return professorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
    }

    public Professor criar(Professor professor) {
        return professorRepository.save(professor);
    }

    public Professor atualizar(Long id, Professor atualizado) {
        Professor professor = buscarPorId(id);

        professor.setNome(atualizado.getNome());
        professor.setEmail(atualizado.getEmail());
        professor.setDepartamento(atualizado.getDepartamento());
        professor.setMatricula(atualizado.getMatricula());

        return professorRepository.save(professor);
    }

    public void deletar(Long id) {
        Professor professor = buscarPorId(id);
        professorRepository.delete(professor);
    }
}