package com.evoxdev.vales_fiados_app.controller;

import com.evoxdev.vales_fiados_app.dto.PreferenciasNotificacaoDTO;
import com.evoxdev.vales_fiados_app.service.PreferenciasNotificacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferencias/notificacoes")
public class PreferenciasNotificacaoController {

    private final PreferenciasNotificacaoService preferenciaService;

    public PreferenciasNotificacaoController(PreferenciasNotificacaoService preferenciaService) {
        this.preferenciaService = preferenciaService;
    }

    /**
     * Obtém as preferências de notificação do usuário autenticado
     */
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PreferenciasNotificacaoDTO> obterMinhasPreferencias(Authentication authentication) {
        String cpf = authentication.getName();
        return ResponseEntity.ok(preferenciaService.obterPreferenciasPorCpf(cpf));
    }

    /**
     * Atualiza as preferências de notificação do usuário autenticado
     */
    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PreferenciasNotificacaoDTO> atualizarMinhasPreferencias(
            @RequestBody PreferenciasNotificacaoDTO preferenciasDTO,
            Authentication authentication) {
        String cpf = authentication.getName();
        return ResponseEntity.ok(preferenciaService.atualizarPreferencias(cpf, preferenciasDTO));
    }
}