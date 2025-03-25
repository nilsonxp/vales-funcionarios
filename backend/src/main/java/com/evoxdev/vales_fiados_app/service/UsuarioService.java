package com.evoxdev.vales_fiados_app.service;

import com.evoxdev.vales_fiados_app.dto.UsuarioDTO;
import com.evoxdev.vales_fiados_app.entity.Usuario;
import com.evoxdev.vales_fiados_app.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void cadastrarUsuario(UsuarioDTO usuarioDTO) {
        // Validar CPF único
        if (usuarioRepository.existsByCpf(usuarioDTO.getCpf())) {
            throw new RuntimeException("Já existe um usuário com esse CPF.");
        }

        // Validar email único, se fornecido
        if (usuarioDTO.getEmail() != null && !usuarioDTO.getEmail().isEmpty() &&
                usuarioRepository.findByEmail(usuarioDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Já existe um usuário com esse email.");
        }

        // Criar e mapear objeto Usuario
        Usuario usuario = new Usuario();
        usuario.setNome(usuarioDTO.getNome());
        usuario.setCpf(usuarioDTO.getCpf());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha())); // Codificar a senha
        usuario.setTelefone(usuarioDTO.getTelefone());
        usuario.setRole(usuarioDTO.getRole());

        usuarioRepository.save(usuario);
    }

    // Adicionar outros métodos como atualizar, buscar, etc.
}