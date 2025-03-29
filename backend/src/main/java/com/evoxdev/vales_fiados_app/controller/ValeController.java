package com.evoxdev.vales_fiados_app.controller;

import com.evoxdev.vales_fiados_app.dto.ValeDTO;
import com.evoxdev.vales_fiados_app.service.ValeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vales")
@Tag(name = "Vales")
public class ValeController {

    private final ValeService valeService;

    public ValeController(ValeService valeService) {
        this.valeService = valeService;
    }

    @PostMapping("/{cpf}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar vale", description = "Cria um novo vale para um usuário específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vale criado com sucesso",
                    content = @Content(schema = @Schema(implementation = ValeDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para criar vale"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<ValeDTO> criarVale(
            @Parameter(description = "CPF do usuário") @PathVariable String cpf,
            @Parameter(description = "Dados do vale") @RequestBody ValeDTO valeDTO,
            Authentication authentication) {
        String criadoPorCpf = authentication.getName();
        ValeDTO novoVale = valeService.criarVale(cpf, valeDTO, criadoPorCpf);
        return ResponseEntity.ok(novoVale);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Listar meus vales", description = "Lista todos os vales do usuário logado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de vales retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para acessar os vales")
    })
    public ResponseEntity<List<ValeDTO>> listarMeusVales(Authentication authentication) {
        String cpf = authentication.getName();
        return ResponseEntity.ok(valeService.listarDTOsPorUsuario(cpf));
    }

    @PatchMapping("/{id}/quitar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Quitar vale", description = "Marca um vale como quitado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vale quitado com sucesso",
                    content = @Content(schema = @Schema(implementation = ValeDTO.class))),
            @ApiResponse(responseCode = "400", description = "Vale já está quitado ou outra condição inválida"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para quitar vale"),
            @ApiResponse(responseCode = "404", description = "Vale não encontrado")
    })
    public ResponseEntity<ValeDTO> quitarVale(@Parameter(description = "ID do vale") @PathVariable Long id) {
        ValeDTO valeQuitado = valeService.marcarComoPago(id);
        return ResponseEntity.ok(valeQuitado);
    }
}