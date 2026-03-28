package com.ifba.sciencefair.service;

import com.ifba.sciencefair.entity.Feira;
import com.ifba.sciencefair.repository.FeiraRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeiraService {

    private final FeiraRepository feiraRepository;

    public FeiraService(FeiraRepository feiraRepository) {
        this.feiraRepository = feiraRepository;
    }

    public List<Feira> listarTodas() {
        return feiraRepository.findAll();
    }

    public Feira buscarPorId(Long id) {
        return feiraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feira não encontrada"));
    }

    public Feira criar(Feira feira) {
        return feiraRepository.save(feira);
    }

    public Feira atualizar(Long id, Feira feiraAtualizada) {
        Feira feira = buscarPorId(id);

        feira.setNome(feiraAtualizada.getNome());
        feira.setDescricao(feiraAtualizada.getDescricao());
        feira.setDataInicio(feiraAtualizada.getDataInicio());
        feira.setDataFim(feiraAtualizada.getDataFim());
        feira.setLocal(feiraAtualizada.getLocal());

        return feiraRepository.save(feira);
    }

    public void deletar(Long id) {
        Feira feira = buscarPorId(id);
        feiraRepository.delete(feira);
    }
}