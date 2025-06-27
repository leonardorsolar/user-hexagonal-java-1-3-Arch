package com.example.hexagonal.service.impl;

import com.example.hexagonal.dto.CreateUsuarioDTO;
import com.example.hexagonal.dto.UpdateUsuarioDTO;
import com.example.hexagonal.dto.UsuarioDTO;
import com.example.hexagonal.entity.Usuario;
import com.example.hexagonal.exception.EmailJaExisteException;
import com.example.hexagonal.exception.UsuarioNotFoundException;
import com.example.hexagonal.mapper.UsuarioMapper;
import com.example.hexagonal.repository.UsuarioRepository;
import com.example.hexagonal.service.UsuarioService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
            UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public UsuarioDTO criar(CreateUsuarioDTO createUsuarioDTO) {
        // Validar se email já existe
        if (emailJaExiste(createUsuarioDTO.getEmail())) {
            throw new EmailJaExisteException(createUsuarioDTO.getEmail());
        }

        // Converter DTO para Entity
        Usuario usuario = usuarioMapper.toEntity(createUsuarioDTO);

        // Criptografar senha
        usuario.setSenha(passwordEncoder.encode(createUsuarioDTO.getSenha()));

        // Normalizar email
        usuario.setEmail(createUsuarioDTO.getEmail().toLowerCase().trim());

        // Salvar no banco
        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        // Converter para DTO e retornar
        return usuarioMapper.toDTO(usuarioSalvo);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));

        return usuarioMapper.toDTO(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new UsuarioNotFoundException("email", email));

        if (!usuario.getAtivo()) {
            throw new UsuarioNotFoundException("email", email);
        }

        return usuarioMapper.toDTO(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarTodos() {
        List<Usuario> usuarios = usuarioRepository.findByAtivoTrue();
        return usuarioMapper.toDTOList(usuarios);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> buscarPorNome(String nome) {
        List<Usuario> usuarios = usuarioRepository.findByNomeContainingIgnoreCase(nome);

        // Filtrar apenas usuários ativos
        List<Usuario> usuariosAtivos = usuarios.stream()
                .filter(Usuario::getAtivo)
                .toList();

        return usuarioMapper.toDTOList(usuariosAtivos);
    }

    @Override
    public UsuarioDTO atualizar(Long id, UpdateUsuarioDTO updateUsuarioDTO) {
        // Buscar usuário existente
        Usuario usuario = usuarioRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));

        // Validar email se foi informado
        if (updateUsuarioDTO.getEmail() != null &&
                !updateUsuarioDTO.getEmail().trim().isEmpty()) {

            String novoEmail = updateUsuarioDTO.getEmail().toLowerCase().trim();

            if (emailJaExisteParaOutroUsuario(novoEmail, id)) {
                throw new EmailJaExisteException(novoEmail);
            }
        }

        // Atualizar dados
        usuarioMapper.updateEntity(usuario, updateUsuarioDTO);

        // Salvar alterações
        Usuario usuarioAtualizado = usuarioRepository.save(usuario);

        return usuarioMapper.toDTO(usuarioAtualizado);
    }

    @Override
    public void inativar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));

        if (!usuario.getAtivo()) {
            throw new UsuarioNotFoundException(id);
        }

        usuario.inativar();
        usuarioRepository.save(usuario);
    }

    @Override
    public UsuarioDTO reativar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));

        if (usuario.getAtivo()) {
            throw new IllegalStateException("Usuário já está ativo");
        }

        usuario.setAtivo(true);
        Usuario usuarioReativado = usuarioRepository.save(usuario);

        return usuarioMapper.toDTO(usuarioReativado);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean emailJaExiste(String email) {
        return usuarioRepository.existsByEmail(email.toLowerCase().trim());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean emailJaExisteParaOutroUsuario(String email, Long userId) {
        return usuarioRepository.existsByEmailAndIdNot(email.toLowerCase().trim(), userId);
    }
}
