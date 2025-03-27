package com.evoxdev.vales_fiados_app.repository;

import com.evoxdev.vales_fiados_app.entity.Notificacao;
import com.evoxdev.vales_fiados_app.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    List<Notificacao> findByUsuarioOrderByDataCriacaoDesc(Usuario usuario);

    List<Notificacao> findByUsuarioAndLidaFalseOrderByDataCriacaoDesc(Usuario usuario);

    long countByUsuarioAndLidaFalse(Usuario usuario);
}