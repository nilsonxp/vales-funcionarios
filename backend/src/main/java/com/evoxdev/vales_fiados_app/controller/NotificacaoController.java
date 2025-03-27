package com.evoxdev.vales_fiados_app.controller;

import com.evoxdev.vales_fiados_app.dto.NotificacaoDTO;
import com.evoxdev.vales_fiados_app.service.NotificacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    public NotificacaoController(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    /**
     * Retorna todas as notificações do usuário autenticado
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<NotificacaoDTO>> listarTodasNotificacoes(Authentication authentication) {
        String cpf = authentication.getName();
        return ResponseEntity.ok(notificacaoService.listarTodasNotificacoes(cpf));
    }

    /**
     * Retorna apenas as notificações não lidas do usuário autenticado
     */
    @GetMapping("/nao-lidas")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<NotificacaoDTO>> listarNotificacoesNaoLidas(Authentication authentication) {
        String cpf = authentication.getName();
        return ResponseEntity.ok(notificacaoService.listarNotificacoesNaoLidas(cpf));
    }

    /**
     * Retorna o número de notificações não lidas
     */
    @GetMapping("/contador")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Long>> contarNotificacoesNaoLidas(Authentication authentication) {
        String cpf = authentication.getName();
        long contador = notificacaoService.contarNotificacoesNaoLidas(cpf);
        return ResponseEntity.ok(Map.of("naoLidas", contador));
    }

    /**
     * Marca uma notificação específica como lida
     */
    @PatchMapping("/{id}/ler")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<NotificacaoDTO> marcarComoLida(
            @PathVariable Long id,
            Authentication authentication) {
        String cpf = authentication.getName();
        NotificacaoDTO notificacao = notificacaoService.marcarComoLida(id, cpf);
        return ResponseEntity.ok(notificacao);
    }

    /**
     * Marca todas as notificações do usuário como lidas
     */
    @PatchMapping("/ler-todas")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, String>> marcarTodasComoLidas(Authentication authentication) {
        String cpf = authentication.getName();
        notificacaoService.marcarTodasComoLidas(cpf);
        return ResponseEntity.ok(Map.of("mensagem", "Todas as notificações foram marcadas como lidas"));
    }
}