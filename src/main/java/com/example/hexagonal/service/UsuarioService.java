package com.example.hexagonal.service;

import com.example.hexagonal.dto.CreateUsuarioDTO;
import com.example.hexagonal.dto.UpdateUsuarioDTO;
import com.example.hexagonal.dto.UsuarioDTO;

import java.util.List;

/**
 * Interface para operações de negócio relacionadas a usuários
 */
public interface UsuarioService {

    /**
     * Criar novo usuário
     */
    UsuarioDTO criar(CreateUsuarioDTO createUsuarioDTO);

    /**
     * Buscar usuário por ID
     */
    UsuarioDTO buscarPorId(Long id);

    /**
     * Buscar usuário por email
     */
    UsuarioDTO buscarPorEmail(String email);

    /**
     * Listar todos os usuários ativos
     */
    List<UsuarioDTO> listarTodos();

    /**
     * Listar usuários por nome
     */
    List<UsuarioDTO> buscarPorNome(String nome);

    /**
     * Atualizar usuário
     */
    UsuarioDTO atualizar(Long id, UpdateUsuarioDTO updateUsuarioDTO);

    /**
     * Inativar usuário (soft delete)
     */
    void inativar(Long id);

    /**
     * Reativar usuário
     */
    UsuarioDTO reativar(Long id);

    /**
     * Verificar se email já existe
     */
    boolean emailJaExiste(String email);

    /**
     * Verificar se email já existe para outro usuário
     */
    boolean emailJaExisteParaOutroUsuario(String email, Long userId);
}