package com.evoxdev.vales_fiados_app.controller;

import com.evoxdev.vales_fiados_app.dto.ValeDTO;
import com.evoxdev.vales_fiados_app.service.ValeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vales")
public class ValeController {

    private final ValeService valeService;

    public ValeController(ValeService valeService) {
        this.valeService = valeService;
    }

    @PostMapping("/{cpf}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ValeDTO> criarVale(
            @PathVariable String cpf,
            @RequestBody ValeDTO valeDTO,
            Authentication authentication) {
        String criadoPorCpf = authentication.getName();
        ValeDTO novoVale = valeService.criarVale(cpf, valeDTO, criadoPorCpf);
        return ResponseEntity.ok(novoVale);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ValeDTO>> listarMeusVales(Authentication authentication) {
        String cpf = authentication.getName();
        return ResponseEntity.ok(valeService.listarDTOsPorUsuario(cpf));
    }

    @PatchMapping("/{id}/quitar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ValeDTO> quitarVale(@PathVariable Long id) {
        ValeDTO valeQuitado = valeService.marcarComoPago(id);
        return ResponseEntity.ok(valeQuitado);
    }
}