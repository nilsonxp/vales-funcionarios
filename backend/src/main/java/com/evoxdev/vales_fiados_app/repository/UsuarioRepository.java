package com.evoxdev.vales_fiados_app.repository;

import com.evoxdev.vales_fiados_app.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByCpf(String cpf);

    Optional<Usuario> findByCpf(String cpf);
    Optional<Usuario> findByEmail(String email);
}
