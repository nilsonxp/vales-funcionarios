package com.evoxdev.vales_fiados_app.controller;

import com.evoxdev.vales_fiados_app.dto.AuthenticationRequest;
import com.evoxdev.vales_fiados_app.dto.AuthenticationResponse;
import com.evoxdev.vales_fiados_app.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}