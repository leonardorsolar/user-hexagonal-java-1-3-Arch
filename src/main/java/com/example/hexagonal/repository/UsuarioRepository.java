package com.example.hexagonal.repository;

import com.example.hexagonal.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca usuário por email
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica se existe usuário com o email
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuários ativos
     */
    List<Usuario> findByAtivoTrue();

    /**
     * Busca usuários inativos (soft delete)
     */
    List<Usuario> findByAtivoFalse();

    /**
     * Busca usuários por nome (case insensitive)
     */
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Usuario> findByNomeContainingIgnoreCase(@Param("nome") String nome);

    /**
     * Verifica se existe outro usuário com o mesmo email (para updates)
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Usuario u WHERE u.email = :email AND u.id <> :id")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("id") Long id);

    /**
     * Busca usuário ativo por ID
     */
    @Query("SELECT u FROM Usuario u WHERE u.id = :id AND u.ativo = true")
    Optional<Usuario> findByIdAndAtivoTrue(@Param("id") Long id);
}
