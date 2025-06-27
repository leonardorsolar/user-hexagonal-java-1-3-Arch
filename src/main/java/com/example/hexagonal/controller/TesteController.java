package com.example.hexagonal.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TesteController {

    @GetMapping("/health")
    public String health() {
        return "Aplicação Spring Boot com SQLite funcionando!";
    }

    @GetMapping("/status")
    public String status() {
        return "Status: OK - Banco SQLite configurado";
    }
}
