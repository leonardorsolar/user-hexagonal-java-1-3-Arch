package com.example.hexagonal.exception;

public class EmailJaExisteException extends RuntimeException {

    public EmailJaExisteException(String email) {
        super("Email '" + email + "' já está sendo usado por outro usuário");
    }
}