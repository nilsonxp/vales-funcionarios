package com.evoxdev.vales_fiados_app.service;

import com.evoxdev.vales_fiados_app.dto.UsuarioDTO;
import com.evoxdev.vales_fiados_app.dto.PerfilDTO;
import com.evoxdev.vales_fiados_app.entity.Usuario;
import com.evoxdev.vales_fiados_app.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.evoxdev.vales_fiados_app.dto.UsuarioResponseDTO;

import java.util.List;

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

    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream().map(usuario -> {
            UsuarioResponseDTO dto = new UsuarioResponseDTO();
            dto.setId(usuario.getId());
            dto.setNome(usuario.getNome());
            dto.setCpf(usuario.getCpf());
            dto.setEmail(usuario.getEmail());
            dto.setTelefone(usuario.getTelefone());
            dto.setRole(usuario.getRole());
            return dto;
        }).toList();
    }

    public PerfilDTO buscarPerfilPorCpf(String cpf) {
        Usuario usuario = usuarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return new PerfilDTO(
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getRole()
        );
    }
}