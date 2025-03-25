package com.evoxdev.vales_fiados_app.controller;

import com.evoxdev.vales_fiados_app.dto.UsuarioDTO;
import com.evoxdev.vales_fiados_app.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrarUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        try {
            usuarioService.cadastrarUsuario(usuarioDTO);
            return ResponseEntity.ok("Usuário cadastrado com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
