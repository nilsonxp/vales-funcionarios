package com.evoxdev.vales_fiados_app.repository;

import com.evoxdev.vales_fiados_app.entity.Auditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    Page<Auditoria> findAllByOrderByDataHoraDesc(Pageable pageable);

    List<Auditoria> findByUsuarioCpf(String cpf);

    List<Auditoria> findByTipoAcao(String tipoAcao);

    List<Auditoria> findByEntidadeAfetadaAndIdEntidadeAfetada(String entidade, Long id);

    @Query("SELECT a FROM Auditoria a WHERE a.dataHora BETWEEN :inicio AND :fim ORDER BY a.dataHora DESC")
    List<Auditoria> findByPeriodo(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    @Query("SELECT a FROM Auditoria a WHERE " +
            "(:tipoAcao IS NULL OR a.tipoAcao = :tipoAcao) AND " +
            "(:cpf IS NULL OR a.usuarioCpf = :cpf) AND " +
            "(:entidade IS NULL OR a.entidadeAfetada = :entidade) AND " +
            "(:idEntidade IS NULL OR a.idEntidadeAfetada = :idEntidade) AND " +
            "a.dataHora BETWEEN :dataInicio AND :dataFim " +
            "ORDER BY a.dataHora DESC")
    Page<Auditoria> buscarComFiltros(
            @Param("tipoAcao") String tipoAcao,
            @Param("cpf") String cpf,
            @Param("entidade") String entidade,
            @Param("idEntidade") Long idEntidade,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable);
}