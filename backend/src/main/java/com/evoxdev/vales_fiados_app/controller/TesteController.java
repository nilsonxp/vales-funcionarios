package com.evoxdev.vales_fiados_app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teste")
asdpublic class TesteController {

    @GetMapping
    public String hello() {
        return "Swagger funcionou!";
    }
}
