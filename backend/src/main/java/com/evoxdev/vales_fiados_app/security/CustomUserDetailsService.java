package com.evoxdev.vales_fiados_app.security;

import com.evoxdev.vales_fiados_app.entity.Usuario;
import com.evoxdev.vales_fiados_app.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String cpf) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com CPF: " + cpf));

        System.out.println("Role do usuário: " + usuario.getRole());

        return User.withUsername(usuario.getCpf())
                .password(usuario.getSenha())
                .authorities("ROLE_" + usuario.getRole())
                .build();
    }
}