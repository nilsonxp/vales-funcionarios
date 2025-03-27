package com.evoxdev.vales_fiados_app.repository;

import com.evoxdev.vales_fiados_app.entity.Vale;
import com.evoxdev.vales_fiados_app.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ValeRepository extends JpaRepository<Vale, Long> {
    List<Vale> findByUsuario(Usuario usuario);
}
