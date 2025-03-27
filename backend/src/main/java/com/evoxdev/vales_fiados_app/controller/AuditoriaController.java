package com.evoxdev.vales_fiados_app.controller;

import com.evoxdev.vales_fiados_app.entity.Auditoria;
import com.evoxdev.vales_fiados_app.service.AuditoriaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auditoria")
@PreAuthorize("hasRole('ADMIN')")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public ResponseEntity<Page<Auditoria>> buscarLogs(
            @RequestParam(required = false) String tipoAcao,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String entidade,
            @RequestParam(required = false) Long idEntidade,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataHora"));

        Page<Auditoria> logs = auditoriaService.buscarComFiltros(
                tipoAcao,
                cpf,
                entidade,
                idEntidade,
                dataInicio,
                dataFim,
                pageRequest
        );

        return ResponseEntity.ok(logs);
    }
}