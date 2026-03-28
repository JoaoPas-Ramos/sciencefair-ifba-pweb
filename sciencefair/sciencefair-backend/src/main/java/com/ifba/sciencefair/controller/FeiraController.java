package com.ifba.sciencefair.controller;

import com.ifba.sciencefair.entity.Feira;
import com.ifba.sciencefair.service.FeiraService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feiras")
@CrossOrigin(origins = "http://localhost:5173")
public class FeiraController {

    private final FeiraService feiraService;

    public FeiraController(FeiraService feiraService) {
        this.feiraService = feiraService;
    }

    @GetMapping
    public ResponseEntity<List<Feira>> listarTodas() {
        return ResponseEntity.ok(feiraService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Feira> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(feiraService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Feira> criar(@RequestBody Feira feira) {
        return ResponseEntity.status(HttpStatus.CREATED).body(feiraService.criar(feira));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Feira> atualizar(@PathVariable Long id, @RequestBody Feira feira) {
        return ResponseEntity.ok(feiraService.atualizar(id, feira));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        feiraService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}