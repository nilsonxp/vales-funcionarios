package com.evoxdev.vales_fiados_app.service;

import com.evoxdev.vales_fiados_app.dto.PreferenciasNotificacaoDTO;
import com.evoxdev.vales_fiados_app.entity.PreferenciasNotificacao;
import com.evoxdev.vales_fiados_app.entity.Usuario;
import com.evoxdev.vales_fiados_app.repository.PreferenciasNotificacaoRepository;
import com.evoxdev.vales_fiados_app.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PreferenciasNotificacaoService {

    private final PreferenciasNotificacaoRepository preferenciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;

    public PreferenciasNotificacaoService(
            PreferenciasNotificacaoRepository preferenciaRepository,
            UsuarioRepository usuarioRepository,
            AuditoriaService auditoriaService) {
        this.preferenciaRepository = preferenciaRepository;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaService = auditoriaService;
    }

    /**
     * Obtém as preferências do usuário pelo CPF
     */
    @Transactional(readOnly = true)
    public PreferenciasNotificacaoDTO obterPreferenciasPorCpf(String cpf) {
        Usuario usuario = usuarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        PreferenciasNotificacao preferencias = preferenciaRepository.findByUsuario(usuario)
                .orElseGet(() -> criarPreferenciasDefault(usuario));

        return converterParaDTO(preferencias);
    }

    /**
     * Atualiza as preferências do usuário
     */
    @Transactional
    public PreferenciasNotificacaoDTO atualizarPreferencias(String cpf, PreferenciasNotificacaoDTO dto) {
        Usuario usuario = usuarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        PreferenciasNotificacao preferencias = preferenciaRepository.findByUsuario(usuario)
                .orElseGet(() -> criarPreferenciasDefault(usuario));

        // Atualizar campos
        preferencias.setReceberNotificacaoValeCriado(dto.isReceberNotificacaoValeCriado());
        preferencias.setReceberNotificacaoValeQuitado(dto.isReceberNotificacaoValeQuitado());
        preferencias.setReceberResumoDiario(dto.isReceberResumoDiario());
        preferencias.setReceberNotificacaoEmail(dto.isReceberNotificacaoEmail());
        preferencias.setReceberNotificacaoApp(dto.isReceberNotificacaoApp());
        preferencias.setEmailAlternativo(dto.getEmailAlternativo());

        preferencias = preferenciaRepository.save(preferencias);

        // Registrar na auditoria
        auditoriaService.registrarAcao(
                "ATUALIZAR_PREFERENCIAS_NOTIFICACAO",
                "Preferências de notificação atualizadas",
                "Usuario",
                usuario.getId(),
                "Usuário: " + usuario.getNome() + " (CPF: " + usuario.getCpf() + ")"
        );

        return converterParaDTO(preferencias);
    }

    /**
     * Cria configurações padrão para um novo usuário
     */
    @Transactional
    public PreferenciasNotificacao criarPreferenciasDefault(Usuario usuario) {
        if (preferenciaRepository.existsByUsuario(usuario)) {
            return preferenciaRepository.findByUsuario(usuario).get();
        }

        PreferenciasNotificacao preferencias = new PreferenciasNotificacao();
        preferencias.setUsuario(usuario);
        preferencias.setReceberNotificacaoValeCriado(true);
        preferencias.setReceberNotificacaoValeQuitado(true);
        preferencias.setReceberResumoDiario(false);
        preferencias.setReceberNotificacaoEmail(false);
        preferencias.setReceberNotificacaoApp(true);
        preferencias.setEmailAlternativo(usuario.getEmail());

        return preferenciaRepository.save(preferencias);
    }

    /**
     * Converte entidade para DTO
     */
    private PreferenciasNotificacaoDTO converterParaDTO(PreferenciasNotificacao preferencias) {
        PreferenciasNotificacaoDTO dto = new PreferenciasNotificacaoDTO();
        dto.setId(preferencias.getId());
        dto.setUsuarioId(preferencias.getUsuario().getId());
        dto.setReceberNotificacaoValeCriado(preferencias.isReceberNotificacaoValeCriado());
        dto.setReceberNotificacaoValeQuitado(preferencias.isReceberNotificacaoValeQuitado());
        dto.setReceberResumoDiario(preferencias.isReceberResumoDiario());
        dto.setReceberNotificacaoEmail(preferencias.isReceberNotificacaoEmail());
        dto.setReceberNotificacaoApp(preferencias.isReceberNotificacaoApp());
        dto.setEmailAlternativo(preferencias.getEmailAlternativo());
        return dto;
    }

    /**
     * Verifica se o usuário deve receber determinado tipo de notificação
     */
    @Transactional(readOnly = true)
    public boolean deveReceberNotificacao(String cpf, String tipoNotificacao) {
        try {
            Usuario usuario = usuarioRepository.findByCpf(cpf)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            PreferenciasNotificacao preferencias = preferenciaRepository.findByUsuario(usuario)
                    .orElseGet(() -> criarPreferenciasDefault(usuario));

            // Verifica se notificações no app estão habilitadas
            if (!preferencias.isReceberNotificacaoApp()) {
                return false;
            }

            // Verifica o tipo específico de notificação
            switch (tipoNotificacao) {
                case "VALE_CRIADO":
                    return preferencias.isReceberNotificacaoValeCriado();
                case "VALE_QUITADO":
                    return preferencias.isReceberNotificacaoValeQuitado();
                default:
                    return true; // Para outros tipos, permitir por padrão
            }
        } catch (Exception e) {
            // Se ocorrer algum erro, permitir a notificação por padrão
            return true;
        }
    }
}