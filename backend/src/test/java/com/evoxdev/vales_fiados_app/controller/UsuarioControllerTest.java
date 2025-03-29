package com.evoxdev.vales_fiados_app.controller;

import com.evoxdev.vales_fiados_app.dto.PerfilDTO;
import com.evoxdev.vales_fiados_app.dto.UsuarioDTO;
import com.evoxdev.vales_fiados_app.dto.UsuarioResponseDTO;
import com.evoxdev.vales_fiados_app.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    private UsuarioDTO usuarioDTO;
    private UsuarioResponseDTO usuarioResponseDTO;
    private PerfilDTO perfilDTO;

    @BeforeEach
    void setUp() {
        // Configurar objetos para testes
        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setNome("Usuário Teste");
        usuarioDTO.setCpf("12345678901");
        usuarioDTO.setEmail("usuario@teste.com");
        usuarioDTO.setSenha("senha123");
        usuarioDTO.setTelefone("(11) 99999-9999");
        usuarioDTO.setRole("USER");

        usuarioResponseDTO = new UsuarioResponseDTO();
        usuarioResponseDTO.setId(1L);
        usuarioResponseDTO.setNome("Usuário Teste");
        usuarioResponseDTO.setCpf("12345678901");
        usuarioResponseDTO.setEmail("usuario@teste.com");
        usuarioResponseDTO.setTelefone("(11) 99999-9999");
        usuarioResponseDTO.setRole("USER");

        perfilDTO = new PerfilDTO("Usuário Teste", "usuario@teste.com", "(11) 99999-9999", "USER");
    }

    @Test
    public void testCadastrarUsuario() throws Exception {
        // Configuração
        doNothing().when(usuarioService).cadastrarUsuario(any(UsuarioDTO.class));

        // Execução & Verificação
        mockMvc.perform(post("/api/usuarios/cadastrar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuário cadastrado com sucesso!"));

        verify(usuarioService, times(1)).cadastrarUsuario(any(UsuarioDTO.class));
    }

    @Test
    public void testCadastrarUsuarioComErro() throws Exception {
        // Configuração
        doThrow(new RuntimeException("CPF já existe")).when(usuarioService).cadastrarUsuario(any(UsuarioDTO.class));

        // Execução & Verificação
        mockMvc.perform(post("/api/usuarios/cadastrar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("CPF já existe"));

        verify(usuarioService, times(1)).cadastrarUsuario(any(UsuarioDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testListarTodos() throws Exception {
        // Configuração
        List<UsuarioResponseDTO> usuarios = Arrays.asList(usuarioResponseDTO);
        when(usuarioService.listarTodos()).thenReturn(usuarios);

        // Execução & Verificação
        mockMvc.perform(get("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nome", is("Usuário Teste")))
                .andExpect(jsonPath("$[0].cpf", is("12345678901")));

        verify(usuarioService, times(1)).listarTodos();
    }

    @Test
    @WithMockUser(username = "12345678901", roles = "USER")
    public void testGetPerfilLogado() throws Exception {
        // Configuração
        when(usuarioService.buscarPerfilPorCpf(anyString())).thenReturn(perfilDTO);

        // Execução & Verificação
        mockMvc.perform(get("/api/usuarios/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Usuário Teste")))
                .andExpect(jsonPath("$.email", is("usuario@teste.com")))
                .andExpect(jsonPath("$.telefone", is("(11) 99999-9999")))
                .andExpect(jsonPath("$.role", is("USER")));

        verify(usuarioService, times(1)).buscarPerfilPorCpf(anyString());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testListarTodosSemPermissao() throws Exception {
        // Execução & Verificação
        mockMvc.perform(get("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(usuarioService, never()).listarTodos();
    }
}