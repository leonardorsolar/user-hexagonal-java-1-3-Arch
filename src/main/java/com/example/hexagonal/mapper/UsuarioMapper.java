package com.example.hexagonal.mapper;

import com.example.hexagonal.dto.CreateUsuarioDTO;
import com.example.hexagonal.dto.UpdateUsuarioDTO;
import com.example.hexagonal.dto.UsuarioDTO;
import com.example.hexagonal.entity.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UsuarioMapper {

    /**
     * Converte Entity para DTO
     */
    public UsuarioDTO toDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getAtivo(),
                usuario.getDataCriacao(),
                usuario.getDataAtualizacao());
    }

    /**
     * Converte CreateDTO para Entity
     */
    public Usuario toEntity(CreateUsuarioDTO createDTO) {
        if (createDTO == null) {
            return null;
        }

        return new Usuario(
                createDTO.getNome(),
                createDTO.getEmail(),
                createDTO.getSenha());
    }

    /**
     * Atualiza Entity com dados do UpdateDTO
     */
    public void updateEntity(Usuario usuario, UpdateUsuarioDTO updateDTO) {
        if (usuario == null || updateDTO == null) {
            return;
        }

        if (updateDTO.getNome() != null && !updateDTO.getNome().trim().isEmpty()) {
            usuario.setNome(updateDTO.getNome().trim());
        }

        if (updateDTO.getEmail() != null && !updateDTO.getEmail().trim().isEmpty()) {
            usuario.setEmail(updateDTO.getEmail().trim().toLowerCase());
        }
    }

    /**
     * Converte lista de Entity para lista de DTO
     */
    public List<UsuarioDTO> toDTOList(List<Usuario> usuarios) {
        return usuarios.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
