package com.evoxdev.vales_fiados_app.controller;

import com.evoxdev.vales_fiados_app.dto.NotificacaoDTO;
import com.evoxdev.vales_fiados_app.security.CustomUserDetailsService;
import com.evoxdev.vales_fiados_app.security.JwtAuthenticationFilter;
import com.evoxdev.vales_fiados_app.security.JwtTokenProvider;
import com.evoxdev.vales_fiados_app.service.NotificacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NotificacaoController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfigurer.class)
        }
)
public class NotificacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificacaoService notificacaoService;

    // Mocks necessários para o contexto de segurança
    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private NotificacaoDTO notificacaoDTO;
    private List<NotificacaoDTO> notificacaoDTOs;

    @BeforeEach
    void setUp() {
        // Configurar objetos para testes
        notificacaoDTO = new NotificacaoDTO();
        notificacaoDTO.setId(1L);
        notificacaoDTO.setMensagem("Novo vale criado");
        notificacaoDTO.setDataCriacao(LocalDateTime.now());
        notificacaoDTO.setLida(false);
        notificacaoDTO.setTipoNotificacao("VALE_CRIADO");
        notificacaoDTO.setUsuarioId(1L);
        notificacaoDTO.setUsuarioNome("Usuário Teste");

        notificacaoDTOs = Arrays.asList(notificacaoDTO);

        // Mock de autenticação
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(
                org.springframework.security.core.userdetails.User
                        .withUsername("12345678901")
                        .password("senha")
                        .authorities("ROLE_USER")
                        .build()
        );
    }

    @Test
    @WithMockUser(username = "12345678901", roles = "USER")
    public void testListarTodasNotificacoes() throws Exception {
        // Configuração
        when(notificacaoService.listarTodasNotificacoes(anyString())).thenReturn(notificacaoDTOs);

        // Execução & Verificação
        mockMvc.perform(get("/api/notificacoes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].mensagem", is("Novo vale criado")))
                .andExpect(jsonPath("$[0].lida", is(false)));

        verify(notificacaoService, times(1)).listarTodasNotificacoes("12345678901");
    }

    @Test
    @WithMockUser(username = "12345678901", roles = "USER")
    public void testListarNotificacoesNaoLidas() throws Exception {
        // Configuração
        when(notificacaoService.listarNotificacoesNaoLidas(anyString())).thenReturn(notificacaoDTOs);

        // Execução & Verificação
        mockMvc.perform(get("/api/notificacoes/nao-lidas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].lida", is(false)));

        verify(notificacaoService, times(1)).listarNotificacoesNaoLidas("12345678901");
    }

    @Test
    @WithMockUser(username = "12345678901", roles = "USER")
    public void testContarNotificacoesNaoLidas() throws Exception {
        // Configuração
        when(notificacaoService.contarNotificacoesNaoLidas(anyString())).thenReturn(5L);

        // Execução & Verificação
        mockMvc.perform(get("/api/notificacoes/contador")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.naoLidas", is(5)));

        verify(notificacaoService, times(1)).contarNotificacoesNaoLidas("12345678901");
    }

    @Test
    @WithMockUser(username = "12345678901", roles = "USER")
    public void testMarcarComoLida() throws Exception {
        // Configuração
        notificacaoDTO.setLida(true);
        when(notificacaoService.marcarComoLida(anyLong(), anyString())).thenReturn(notificacaoDTO);

        // Execução & Verificação
        mockMvc.perform(patch("/api/notificacoes/1/ler")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.lida", is(true)));

        verify(notificacaoService, times(1)).marcarComoLida(1L, "12345678901");
    }

    @Test
    @WithMockUser(username = "12345678901", roles = "USER")
    public void testMarcarTodasComoLidas() throws Exception {
        // Configuração
        doNothing().when(notificacaoService).marcarTodasComoLidas(anyString());

        // Execução & Verificação
        mockMvc.perform(patch("/api/notificacoes/ler-todas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem", is("Todas as notificações foram marcadas como lidas")));

        verify(notificacaoService, times(1)).marcarTodasComoLidas("12345678901");
    }

    @Test
    public void testAcessoSemAutenticacao() throws Exception {
        // Execução & Verificação
        mockMvc.perform(get("/api/notificacoes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(notificacaoService, times(0)).listarTodasNotificacoes(anyString());
    }
}