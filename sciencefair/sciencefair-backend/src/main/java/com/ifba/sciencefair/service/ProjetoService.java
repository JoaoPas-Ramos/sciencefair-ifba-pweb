package com.ifba.sciencefair.service;

import com.ifba.sciencefair.entity.Feira;
import com.ifba.sciencefair.entity.Professor;
import com.ifba.sciencefair.entity.Projeto;
import com.ifba.sciencefair.repository.FeiraRepository;
import com.ifba.sciencefair.repository.ProfessorRepository;
import com.ifba.sciencefair.repository.ProjetoRepository;
import org.springframework.stereotype.Service;
import com.ifba.sciencefair.entity.Professor;
import java.util.List;

@Service
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final FeiraRepository feiraRepository;
    private final ProfessorRepository professorRepository;

    public ProjetoService(ProjetoRepository projetoRepository,
                          FeiraRepository feiraRepository,
                          ProfessorRepository professorRepository) {
        this.projetoRepository = projetoRepository;
        this.feiraRepository = feiraRepository;
        this.professorRepository = professorRepository;
    }

    public List<Projeto> listarTodos() {
        return projetoRepository.findAll();
    }

    public Projeto buscarPorId(Long id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));
    }

    public Projeto criar(Projeto projeto) {

        // buscar feira
        Long feiraId = projeto.getFeira().getId();
        Feira feira = feiraRepository.findById(feiraId)
                .orElseThrow(() -> new RuntimeException("Feira não encontrada"));

        // buscar professor
        Long professorId = projeto.getProfessor().getId();
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        // setar objetos completos
        projeto.setFeira(feira);
        projeto.setProfessor(professor);

        return projetoRepository.save(projeto);
    }

    public Projeto atualizar(Long id, Projeto atualizado) {

        Projeto projeto = buscarPorId(id);

        // buscar feira
        Long feiraId = atualizado.getFeira().getId();
        Feira feira = feiraRepository.findById(feiraId)
                .orElseThrow(() -> new RuntimeException("Feira não encontrada"));

        // buscar professor
        Long professorId = atualizado.getProfessor().getId();
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        // atualizar campos
        projeto.setTitulo(atualizado.getTitulo());
        projeto.setDescricao(atualizado.getDescricao());
        projeto.setAreaTematica(atualizado.getAreaTematica());
        projeto.setStatus(atualizado.getStatus());
        projeto.setFeira(feira);
        projeto.setProfessor(professor);

        return projetoRepository.save(projeto);
    }

    public void deletar(Long id) {
        Projeto projeto = buscarPorId(id);
        projetoRepository.delete(projeto);
    }
}