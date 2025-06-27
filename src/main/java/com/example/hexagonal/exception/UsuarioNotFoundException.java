package com.example.hexagonal.exception;

public class UsuarioNotFoundException extends RuntimeException {

    public UsuarioNotFoundException(String message) {
        super(message);
    }

    public UsuarioNotFoundException(Long id) {
        super("Usuário com ID " + id + " não encontrado");
    }

    public UsuarioNotFoundException(String field, String value) {
        super("Usuário com " + field + " '" + value + "' não encontrado");
    }
}