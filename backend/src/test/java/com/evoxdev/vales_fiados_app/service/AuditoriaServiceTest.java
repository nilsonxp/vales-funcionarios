package com.evoxdev.vales_fiados_app.service;

import com.evoxdev.vales_fiados_app.entity.Auditoria;
import com.evoxdev.vales_fiados_app.repository.AuditoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuditoriaServiceTest {

    @Mock
    private AuditoriaRepository auditoriaRepository;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private AuditoriaService auditoriaService;

    private Auditoria auditoria;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private PageRequest pageRequest;
    private Page<Auditoria> auditoriaPage;

    @BeforeEach
    void setUp() {
        // Configurar objetos para testes
        auditoria = new Auditoria();
        auditoria.setId(1L);
        auditoria.setTipoAcao("CRIAR_VALE");
        auditoria.setDescricao("Vale criado para usuário João");
        auditoria.setDataHora(LocalDateTime.now());
        auditoria.setUsuarioCpf("12345678901");
        auditoria.setUsuarioNome("Administrador");
        auditoria.setEntidadeAfetada("Vale");
        auditoria.setIdEntidadeAfetada(1L);
        auditoria.setIpOrigem("127.0.0.1");

        inicio = LocalDateTime.now().minusDays(30);
        fim = LocalDateTime.now();

        pageRequest = PageRequest.of(0, 10);
        auditoriaPage = new PageImpl<>(List.of(auditoria));

        // Mock do SecurityContext
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testRegistrarAcao() {
        // Configuração
        // Criar um usuário autenticado
        User user = new User("12345678901", "senha", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(auditoriaRepository.save(any(Auditoria.class))).thenReturn(auditoria);

        // Execução
        auditoriaService.registrarAcao(
                "CRIAR_VALE",
                "Vale criado para usuário João",
                "Vale",
                1L,
                "Detalhes do vale"
        );

        // Verificação
        ArgumentCaptor<Auditoria> auditoriaCaptor = ArgumentCaptor.forClass(Auditoria.class);
        verify(auditoriaRepository).save(auditoriaCaptor.capture());

        Auditoria auditoriaCapturada = auditoriaCaptor.getValue();
        assertEquals("CRIAR_VALE", auditoriaCapturada.getTipoAcao());
        assertEquals("Vale criado para usuário João", auditoriaCapturada.getDescricao());
        assertEquals("Vale", auditoriaCapturada.getEntidadeAfetada());
        assertEquals(1L, auditoriaCapturada.getIdEntidadeAfetada());
        assertEquals("12345678901", auditoriaCapturada.getUsuarioCpf());
        assertNotNull(auditoriaCapturada.getDataHora());
    }

    @Test
    void testRegistrarAcaoSemUsuarioAutenticado() {
        // Configuração
        when(securityContext.getAuthentication()).thenReturn(null);
        when(auditoriaRepository.save(any(Auditoria.class))).thenReturn(auditoria);

        // Execução
        auditoriaService.registrarAcao(
                "CRIAR_VALE",
                "Vale criado pelo sistema",
                "Vale",
                1L,
                "Detalhes do vale"
        );

        // Verificação
        ArgumentCaptor<Auditoria> auditoriaCaptor = ArgumentCaptor.forClass(Auditoria.class);
        verify(auditoriaRepository).save(auditoriaCaptor.capture());

        Auditoria auditoriaCapturada = auditoriaCaptor.getValue();
        assertEquals("Sistema", auditoriaCapturada.getUsuarioNome());
    }

    @Test
    void testBuscarComFiltros() {
        // Configuração
        when(auditoriaRepository.buscarComFiltros(
                eq("CRIAR_VALE"),
                eq("12345678901"),
                eq("Vale"),
                eq(1L),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                eq(pageRequest)
        )).thenReturn(auditoriaPage);

        // Execução
        Page<Auditoria> resultado = auditoriaService.buscarComFiltros(
                "CRIAR_VALE",
                "12345678901",
                "Vale",
                1L,
                inicio,
                fim,
                pageRequest
        );

        // Verificação
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(auditoria.getId(), resultado.getContent().get(0).getId());
    }

    @Test
    void testBuscarComFiltrosDataInicioPadrao() {
        // Configuração
        when(auditoriaRepository.buscarComFiltros(
                eq("CRIAR_VALE"),
                eq("12345678901"),
                eq("Vale"),
                eq(1L),
                any(LocalDateTime.class),
                eq(fim),
                eq(pageRequest)
        )).thenReturn(auditoriaPage);

        // Execução
        Page<Auditoria> resultado = auditoriaService.buscarComFiltros(
                "CRIAR_VALE",
                "12345678901",
                "Vale",
                1L,
                null,
                fim,
                pageRequest
        );

        // Verificação
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());

        // Verificar se a data inicial padrão foi utilizada (30 dias atrás)
        ArgumentCaptor<LocalDateTime> dataInicioCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(auditoriaRepository).buscarComFiltros(
                eq("CRIAR_VALE"),
                eq("12345678901"),
                eq("Vale"),
                eq(1L),
                dataInicioCaptor.capture(),
                eq(fim),
                eq(pageRequest)
        );

        LocalDateTime dataInicioPadrao = dataInicioCaptor.getValue();
        assertNotNull(dataInicioPadrao);
    }
}