package com.ifba.sciencefair.service;

import com.ifba.sciencefair.dto.RegisterRequest;
import com.ifba.sciencefair.entity.User;
import com.ifba.sciencefair.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado.");
        }

        User user = new User();
        user.setNome(request.getNome());
        user.setEmail(request.getEmail());
        user.setSenha(passwordEncoder.encode(request.getSenha()));
        user.setAtivo(true);
        user.setRole("USER");

        userRepository.save(user);
    }

    public User login(String email, String senha) {

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    if (!passwordEncoder.matches(senha, user.getSenha())) {
        throw new RuntimeException("Senha inválida");
    }

    if (!user.isAtivo()) {
        throw new RuntimeException("Usuário inativo. Aguarde ativação.");
    }

    return user;
}
}

