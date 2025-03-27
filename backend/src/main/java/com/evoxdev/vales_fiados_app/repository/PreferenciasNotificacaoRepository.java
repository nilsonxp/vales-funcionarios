package com.evoxdev.vales_fiados_app.repository;

import com.evoxdev.vales_fiados_app.entity.PreferenciasNotificacao;
import com.evoxdev.vales_fiados_app.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreferenciasNotificacaoRepository extends JpaRepository<PreferenciasNotificacao, Long> {

    Optional<PreferenciasNotificacao> findByUsuario(Usuario usuario);

    Optional<PreferenciasNotificacao> findByUsuarioId(Long usuarioId);

    boolean existsByUsuario(Usuario usuario);
}