package com.ifba.sciencefair.service;

import com.ifba.sciencefair.entity.Post;
import com.ifba.sciencefair.entity.Projeto;
import com.ifba.sciencefair.repository.PostRepository;
import com.ifba.sciencefair.repository.ProjetoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final ProjetoRepository projetoRepository;

    public PostService(PostRepository postRepository, ProjetoRepository projetoRepository) {
        this.postRepository = postRepository;
        this.projetoRepository = projetoRepository;
    }

    public List<Post> listarTodos() {
        return postRepository.findAll();
    }

    public Post buscarPorId(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post não encontrado"));
    }

    public Post criar(Post post) {

        Long projetoId = post.getProjeto().getId();

        Projeto projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        post.setProjeto(projeto);
        post.setDataCriacao(LocalDateTime.now());

        return postRepository.save(post);
    }

    public Post atualizar(Long id, Post atualizado) {

        Post post = buscarPorId(id);

        post.setTitulo(atualizado.getTitulo());
        post.setConteudo(atualizado.getConteudo());

        return postRepository.save(post);
    }

    public void deletar(Long id) {
        Post post = buscarPorId(id);
        postRepository.delete(post);
    }
}