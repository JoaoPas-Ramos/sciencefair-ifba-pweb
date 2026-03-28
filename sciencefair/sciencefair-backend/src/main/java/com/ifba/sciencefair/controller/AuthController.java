package com.ifba.sciencefair.controller;

import com.ifba.sciencefair.dto.MessageResponse;
import com.ifba.sciencefair.dto.RegisterRequest;
import com.ifba.sciencefair.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ifba.sciencefair.dto.LoginRequest;
import com.ifba.sciencefair.entity.User;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("Usuário cadastrado com sucesso. Aguarde ativação."));
    }

@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request) {

    User user = userService.login(request.getEmail(), request.getSenha());

    return ResponseEntity.ok(user);
}}