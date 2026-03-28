package com.ifba.sciencefair.service;

import com.ifba.sciencefair.entity.Aluno;
import com.ifba.sciencefair.entity.Projeto;
import com.ifba.sciencefair.repository.AlunoRepository;
import com.ifba.sciencefair.repository.ProjetoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final ProjetoRepository projetoRepository;

    public AlunoService(AlunoRepository alunoRepository, ProjetoRepository projetoRepository) {
        this.alunoRepository = alunoRepository;
        this.projetoRepository = projetoRepository;
    }

    public List<Aluno> listarTodos() {
        return alunoRepository.findAll();
    }

    public Aluno buscarPorId(Long id) {
        return alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
    }

    public Aluno criar(Aluno aluno) {

        Long projetoId = aluno.getProjeto().getId();

        Projeto projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        aluno.setProjeto(projeto);

        return alunoRepository.save(aluno);
    }

    public Aluno atualizar(Long id, Aluno atualizado) {

        Aluno aluno = buscarPorId(id);

        Long projetoId = atualizado.getProjeto().getId();

        Projeto projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        aluno.setNome(atualizado.getNome());
        aluno.setEmail(atualizado.getEmail());
        aluno.setMatricula(atualizado.getMatricula());
        aluno.setTurma(atualizado.getTurma());
        aluno.setProjeto(projeto);

        return alunoRepository.save(aluno);
    }

    public void deletar(Long id) {
        Aluno aluno = buscarPorId(id);
        alunoRepository.delete(aluno);
    }
}