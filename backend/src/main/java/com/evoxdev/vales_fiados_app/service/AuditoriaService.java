package com.evoxdev.vales_fiados_app.service;

import com.evoxdev.vales_fiados_app.entity.Auditoria;
import com.evoxdev.vales_fiados_app.repository.AuditoriaRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    /**
     * Registra uma ação no log de auditoria
     * Usa PROPAGATION_REQUIRES_NEW para garantir que o log seja salvo mesmo se a transação principal falhar
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarAcao(
            String tipoAcao,
            String descricao,
            String entidadeAfetada,
            Long idEntidadeAfetada,
            String detalhes) {

        try {
            // Obter informações do usuário logado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String cpf = null;
            String nome = "Sistema";

            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                cpf = authentication.getName();
                nome = authentication.getName(); // O ideal seria buscar o nome completo do usuário
            }

            // Obter IP da requisição
            String ipOrigem = getClientIpAddress();

            // Criar o registro de auditoria
            Auditoria auditoria = new Auditoria();
            auditoria.setTipoAcao(tipoAcao);
            auditoria.setDescricao(descricao);
            auditoria.setDataHora(LocalDateTime.now());
            auditoria.setUsuarioCpf(cpf);
            auditoria.setUsuarioNome(nome);
            auditoria.setIpOrigem(ipOrigem);
            auditoria.setEntidadeAfetada(entidadeAfetada);
            auditoria.setIdEntidadeAfetada(idEntidadeAfetada);
            auditoria.setDetalhes(detalhes);

            // Salvar o registro
            auditoriaRepository.save(auditoria);
        } catch (Exception e) {
            // Log de erro, mas não lançar exceção para não afetar a operação principal
            System.err.println("Erro ao registrar auditoria: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Busca registros de auditoria com filtros
     */
    @Transactional(readOnly = true)
    public Page<Auditoria> buscarComFiltros(
            String tipoAcao,
            String cpf,
            String entidade,
            Long idEntidade,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            Pageable pageable) {

        // Se datas não foram informadas, usar um período padrão (últimos 30 dias)
        if (dataInicio == null) {
            dataInicio = LocalDateTime.now().minusDays(30);
        }

        if (dataFim == null) {
            dataFim = LocalDateTime.now();
        }

        return auditoriaRepository.buscarComFiltros(
                tipoAcao,
                cpf,
                entidade,
                idEntidade,
                dataInicio,
                dataFim,
                pageable
        );
    }

    /**
     * Obter o IP do cliente
     */
    private String getClientIpAddress() {
        String ipAddress = "desconhecido";
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                ipAddress = Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                        .orElse(request.getRemoteAddr());
            }
        } catch (Exception e) {
            // Ignora erro ao obter IP
        }
        return ipAddress;
    }
}