package com.example.hexagonal.controller;

import com.example.hexagonal.dto.CreateUsuarioDTO;
import com.example.hexagonal.dto.UpdateUsuarioDTO;
import com.example.hexagonal.dto.UsuarioDTO;
import com.example.hexagonal.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Criar novo usuário
     * POST /api/usuarios
     */
    @PostMapping
    public ResponseEntity<UsuarioDTO> criar(@Valid @RequestBody CreateUsuarioDTO createUsuarioDTO) {
        UsuarioDTO usuarioCriado = usuarioService.criar(createUsuarioDTO);

        URI location = URI.create("/api/usuarios/" + usuarioCriado.getId());

        return ResponseEntity.created(location).body(usuarioCriado);
    }

    // @GetMapping
    // public ResponseEntity<List<UsuarioDTO>> listarTodos() {
    // List<UsuarioDTO> usuarios = usuarioService.listarTodos();
    // return ResponseEntity.ok(usuarios);
    // }

    // @GetMapping("/{id}")
    // public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
    // UsuarioDTO usuario = usuarioService.buscarPorId(id);
    // return ResponseEntity.ok(usuario);
    // }

    // @GetMapping("/buscar")
    // public ResponseEntity<List<UsuarioDTO>> buscarPorNome(@RequestParam String
    // nome) {
    // List<UsuarioDTO> usuarios = usuarioService.buscarPorNome(nome);
    // return ResponseEntity.ok(usuarios);
    // }

    // @PutMapping("/{id}")
    // public ResponseEntity<UsuarioDTO> atualizar(
    // @PathVariable Long id,
    // @Valid @RequestBody UpdateUsuarioDTO dto) {
    // UsuarioDTO atualizado = usuarioService.atualizar(id, dto);
    // return ResponseEntity.ok(atualizado);
    // }

    // @DeleteMapping("/{id}")
    // public ResponseEntity<Void> inativar(@PathVariable Long id) {
    // usuarioService.inativar(id);
    // return ResponseEntity.noContent().build(); // Retorna 204 No Content
    // }

    // @PatchMapping("/{id}/reativar")
    // public ResponseEntity<UsuarioDTO> reativar(@PathVariable Long id) {
    // UsuarioDTO usuario = usuarioService.reativar(id);
    // return ResponseEntity.ok(usuario);
    // }

    // @GetMapping("/email-existe/{email}")
    // public ResponseEntity<Boolean> emailJaExiste(@PathVariable String email) {
    // boolean existe = usuarioService.emailJaExiste(email);
    // return ResponseEntity.ok(existe);
    // }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("API está funcionando!");
    }
}
