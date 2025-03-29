package com.evoxdev.vales_fiados_app.controller;

import com.evoxdev.vales_fiados_app.dto.ValeDTO;
import com.evoxdev.vales_fiados_app.security.CustomUserDetailsService;
import com.evoxdev.vales_fiados_app.security.JwtAuthenticationFilter;
import com.evoxdev.vales_fiados_app.security.JwtTokenProvider;
import com.evoxdev.vales_fiados_app.service.ValeService;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ValeController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfigurer.class)
        }
)
public class ValeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ValeService valeService;

    // Mocks necessários para o contexto de segurança
    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private ValeDTO valeDTO;
    private List<ValeDTO> valeDTOs;

    @BeforeEach
    void setUp() {
        // Configurar objetos para testes
        valeDTO = new ValeDTO();
        valeDTO.setId(1L);
        valeDTO.setDescricao("Vale para despesas");
        valeDTO.setValor(new BigDecimal("100.00"));
        valeDTO.setQuitado(false);
        valeDTO.setCriadoEm(LocalDateTime.now());
        valeDTO.setUsuarioId(1L);
        valeDTO.setUsuarioNome("Usuário Teste");
        valeDTO.setCriadoPorAdm("Admin Teste");

        valeDTOs = Arrays.asList(valeDTO);

        // Mock de autenticação
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(
                org.springframework.security.core.userdetails.User
                        .withUsername("usuarioTeste")
                        .password("senha")
                        .authorities("ROLE_USER")
                        .build()
        );
    }

    @Test
    @WithMockUser(username = "98765432101", roles = "ADMIN")
    public void testCriarVale() throws Exception {
        // Configuração
        when(valeService.criarVale(anyString(), any(ValeDTO.class), anyString())).thenReturn(valeDTO);

        // Execução & Verificação
        mockMvc.perform(post("/api/vales/12345678901")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(valeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.descricao", is("Vale para despesas")))
                .andExpect(jsonPath("$.valor", is(100.00)))
                .andExpect(jsonPath("$.quitado", is(false)));

        verify(valeService, times(1)).criarVale(eq("12345678901"), any(ValeDTO.class), eq("98765432101"));
    }

    @Test
    @WithMockUser(username = "12345678901", roles = "USER")
    public void testListarMeusVales() throws Exception {
        // Configuração
        when(valeService.listarDTOsPorUsuario(anyString())).thenReturn(valeDTOs);

        // Execução & Verificação
        mockMvc.perform(get("/api/vales/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].descricao", is("Vale para despesas")))
                .andExpect(jsonPath("$[0].valor", is(100.00)));

        verify(valeService, times(1)).listarDTOsPorUsuario("12345678901");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testQuitarVale() throws Exception {
        // Configuração
        valeDTO.setQuitado(true);
        when(valeService.marcarComoPago(anyLong())).thenReturn(valeDTO);

        // Execução & Verificação
        mockMvc.perform(patch("/api/vales/1/quitar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.quitado", is(true)));

        verify(valeService, times(1)).marcarComoPago(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testCriarValeSemPermissao() throws Exception {
        // Execução & Verificação
        mockMvc.perform(post("/api/vales/12345678901")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(valeDTO)))
                .andExpect(status().isForbidden());

        verify(valeService, never()).criarVale(anyString(), any(ValeDTO.class), anyString());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testQuitarValeSemPermissao() throws Exception {
        // Execução & Verificação
        mockMvc.perform(patch("/api/vales/1/quitar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(valeService, never()).marcarComoPago(anyLong());
    }
}