# Tutorial 2: Refatoração para Arquitetura Hexagonal

## Java Spring Boot + SQLite + Clean Architecture

### Sumário Executivo

Este tutorial refatora o sistema CRUD de usuários da **arquitetura em 3 camadas** para **Arquitetura Hexagonal** (também conhecida como **Ports and Adapters** ou **Clean Architecture**), aplicando conceitos avançados de **Domain-Driven Design (DDD)** e **princípios de Clean Code**.

---

## Parte 1: Fundamentos da Arquitetura Hexagonal

### 1.1 O que é Arquitetura Hexagonal?

A **Arquitetura Hexagonal** foi criada por **Alistair Cockburn** e tem como objetivo:

```
┌───────────────────────────────────────────────────────────┐
│                     WORLD OUTSIDE                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │   Web API    │  │   Database   │  │  External    │     │
│  │  (Adapter)   │  │  (Adapter)   │  │   Services   │     │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘     │
│         │                 │                 │             │
│  ┌──────▼───────┐  ┌──────▼───────┐  ┌──────▼───────┐     │
│  │ Input Port   │  │Output Port   │  │Output Port   │     │
│  │(Interface)   │  │(Interface)   │  │(Interface)   │     │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘     │
│         │                 │                 │             │
│         └─────────────────┼─────────────────┘             │
│                           │                               │
│    ┌─────────────────────▼─────────────────────┐          │
│    │           HEXAGON (CORE)                  │          │
│    │                                           │          │
│    │  ┌─────────────────────────────────────┐  │          │
│    │  │         APPLICATION LAYER           │  │          │
│    │  │     (Use Cases / Services)          │  │          │
│    │  │                                     │  │          │
│    │  │  ┌─────────────────────────────┐    │  │          │
│    │  │  │       DOMAIN LAYER          │    │  │          │
│    │  │  │   (Entities / Business)     │    │  │          │
│    │  │  │                             │    │  │          │
│    │  │  └─────────────────────────────┘    │  │          │
│    │  └─────────────────────────────────────┘  │          │
│    └───────────────────────────────────────────┘          │
└───────────────────────────────────────────────────────────┘
```

### 1.2 Conceitos Fundamentais

#### **Ports (Portas)**

-   **Input Ports**: Interfaces que definem casos de uso
-   **Output Ports**: Interfaces que definem dependências externas

#### **Adapters (Adaptadores)**

-   **Primary Adapters**: Iniciam ações (Controllers, CLI, etc.)
-   **Secondary Adapters**: Implementam funcionalidades (Database, APIs, etc.)

#### **Core (Núcleo)**

-   **Domain Layer**: Entidades e regras de negócio
-   **Application Layer**: Casos de uso e orquestração

### 1.3 Vantagens da Arquitetura Hexagonal

#### ✅ **Desacoplamento Total**

-   Domínio independente de frameworks
-   Fácil troca de tecnologias (database, web framework, etc.)

#### ✅ **Testabilidade Máxima**

-   Core pode ser testado sem infraestrutura
-   Mocks simples para adapters

#### ✅ **Flexibilidade**

-   Múltiplos adapters (REST API, GraphQL, CLI)
-   Diferentes bancos de dados (SQLite, PostgreSQL, MongoDB)

#### ✅ **Manutenibilidade**

-   Regras de negócio centralizadas
-   Mudanças de infraestrutura não afetam o core

---

## Parte 2: Estrutura da Arquitetura Hexagonal

### 2.1 Nova Estrutura do Projeto

```
src/main/java/com/exemplo/hexagonal/
├── HexagonalApplication.java
├── domain/                     # ← CORE (Hexágono)
│   ├── model/                  # Entidades do domínio
│   │   ├── Usuario.java
│   │   └── Email.java
│   ├── exception/              # Exceções do domínio
│   │   ├── DomainException.java
│   │   ├── UsuarioNotFoundException.java
│   │   └── EmailJaExisteException.java
│   └── service/                # Serviços do domínio
│       └── UsuarioDomainService.java
├── application/                # ← APPLICATION LAYER
│   ├── port/                   # Portas (Interfaces)
│   │   ├── input/              # Input Ports (Casos de Uso)
│   │   │   ├── CriarUsuarioUseCase.java
│   │   │   ├── BuscarUsuarioUseCase.java
│   │   │   ├── AtualizarUsuarioUseCase.java
│   │   │   └── InativarUsuarioUseCase.java
│   │   └── output/             # Output Ports (Dependencies)
│   │       ├── UsuarioRepositoryPort.java
│   │       ├── EmailServicePort.java
│   │       └── PasswordEncoderPort.java
│   ├── service/                # Implementação dos Use Cases
│   │   ├── CriarUsuarioService.java
│   │   ├── BuscarUsuarioService.java
│   │   ├── AtualizarUsuarioService.java
│   │   └── InativarUsuarioService.java
│   └── dto/                    # DTOs da aplicação
│       ├── UsuarioDTO.java
│       ├── CreateUsuarioCommand.java
│       └── UpdateUsuarioCommand.java
└── infrastructure/             # ← INFRASTRUCTURE LAYER
    ├── adapter/                # Adaptadores
    │   ├── input/              # Primary Adapters
    │   │   └── web/
    │   │       ├── UsuarioController.java
    │   │       └── GlobalExceptionHandler.java
    │   └── output/             # Secondary Adapters
    │       ├── persistence/
    │       │   ├── UsuarioJpaRepository.java
    │       │   ├── UsuarioRepositoryAdapter.java
    │       │   └── entity/
    │       │       └── UsuarioEntity.java
    │       ├── encoder/
    │       │   └── BCryptPasswordEncoderAdapter.java
    │       └── email/
    │           └── EmailServiceAdapter.java
    ├── config/                 # Configurações
    │   ├── BeanConfiguration.java
    │   └── DatabaseConfig.java
    └── mapper/                 # Mapeadores
        ├── UsuarioMapper.java
        └── UsuarioEntityMapper.java
```

### 2.2 Comparação: 3 Camadas vs Hexagonal

| Aspecto           | 3 Camadas                         | Hexagonal                            |
| ----------------- | --------------------------------- | ------------------------------------ |
| **Acoplamento**   | Controller → Service → Repository | Ports ↔ Adapters                     |
| **Dependências**  | Camadas dependem de frameworks    | Core independe de tudo               |
| **Testabilidade** | Mocks complexos                   | Mocks simples (interfaces)           |
| **Flexibilidade** | Mudanças afetam múltiplas camadas | Mudanças isoladas em adapters        |
| **Complexidade**  | Menor (inicial)                   | Maior (inicial), menor (longo prazo) |

---

## Parte 3: Implementação do Core (Domain)

### 3.1 Domain Model

#### Email.java (Value Object)

```java
package com.exemplo.hexagonal.domain.model;

import com.exemplo.hexagonal.domain.exception.DomainException;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object para representar um email válido
 */
public class Email {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final String value;

    public Email(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new DomainException("Email não pode ser nulo ou vazio");
        }

        String normalizedEmail = value.trim().toLowerCase();

        if (!EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
            throw new DomainException("Formato de email inválido: " + value);
        }

        this.value = normalizedEmail;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
```

#### Usuario.java (Aggregate Root)

```java
package com.exemplo.hexagonal.domain.model;

import com.exemplo.hexagonal.domain.exception.DomainException;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Aggregate Root do domínio Usuario
 */
public class Usuario {

    private Long id;
    private String nome;
    private Email email;
    private String senhaHash;
    private boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    // Construtor para criação de novo usuário
    public Usuario(String nome, Email email, String senhaHash) {
        this.setNome(nome);
        this.email = Objects.requireNonNull(email, "Email não pode ser nulo");
        this.senhaHash = Objects.requireNonNull(senhaHash, "Hash da senha não pode ser nulo");
        this.ativo = true;
        this.dataCriacao = LocalDateTime.now();
    }

    // Construtor para reconstrução (vindo do banco)
    public Usuario(Long id, String nome, Email email, String senhaHash,
                   boolean ativo, LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.id = id;
        this.setNome(nome);
        this.email = Objects.requireNonNull(email, "Email não pode ser nulo");
        this.senhaHash = Objects.requireNonNull(senhaHash, "Hash da senha não pode ser nulo");
        this.ativo = ativo;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    // Regras de negócio do domínio

    public void atualizarNome(String novoNome) {
        this.setNome(novoNome);
        this.marcarComoAtualizado();
    }

    public void atualizarEmail(Email novoEmail) {
        this.email = Objects.requireNonNull(novoEmail, "Email não pode ser nulo");
        this.marcarComoAtualizado();
    }

    public void inativar() {
        if (!this.ativo) {
            throw new DomainException("Usuário já está inativo");
        }
        this.ativo = false;
        this.marcarComoAtualizado();
    }

    public void ativar() {
        if (this.ativo) {
            throw new DomainException("Usuário já está ativo");
        }
        this.ativo = true;
        this.marcarComoAtualizado();
    }

    public boolean isNovo() {
        return this.id == null;
    }

    public boolean isAtivo() {
        return this.ativo;
    }

    // Métodos privados

    private void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new DomainException("Nome não pode ser nulo ou vazio");
        }

        String nomeNormalizado = nome.trim();

        if (nomeNormalizado.length() < 2) {
            throw new DomainException("Nome deve ter pelo menos 2 caracteres");
        }

        if (nomeNormalizado.length() > 100) {
            throw new DomainException("Nome não pode ter mais de 100 caracteres");
        }

        this.nome = nomeNormalizado;
    }

    private void marcarComoAtualizado() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Getters

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public Email getEmail() { return email; }
    public String getSenhaHash() { return senhaHash; }
    public boolean getAtivo() { return ativo; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }

    // Métodos para reconstrução (usado pelos adapters)
    public void setId(Long id) {
        if (this.id != null) {
            throw new DomainException("ID já foi definido para este usuário");
        }
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email=" + email +
                ", ativo=" + ativo +
                '}';
    }
}
```

### 3.2 Domain Exceptions

#### DomainException.java

```java
package com.exemplo.hexagonal.domain.exception;

/**
 * Exceção base para todas as exceções do domínio
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

#### UsuarioNotFoundException.java

```java
package com.exemplo.hexagonal.domain.exception;

public class UsuarioNotFoundException extends DomainException {

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
```

#### EmailJaExisteException.java

```java
package com.exemplo.hexagonal.domain.exception;

public class EmailJaExisteException extends DomainException {

    public EmailJaExisteException(String email) {
        super("Email '" + email + "' já está sendo usado por outro usuário");
    }
}
```

### 3.3 Domain Service

#### UsuarioDomainService.java

```java
package com.exemplo.hexagonal.domain.service;

import com.exemplo.hexagonal.domain.exception.EmailJaExisteException;
import com.exemplo.hexagonal.domain.model.Email;
import com.exemplo.hexagonal.domain.model.Usuario;

/**
 * Serviço de domínio para regras complexas que envolvem múltiplas entidades
 */
public class UsuarioDomainService {

    /**
     * Valida se um email pode ser usado por um usuário
     */
    public void validarEmailUnico(Email email, Usuario usuarioExistente,
                                  boolean emailJaExiste) {
        // Se é um usuário existente e o email não mudou, pode usar
        if (usuarioExistente != null &&
            usuarioExistente.getEmail().equals(email)) {
            return;
        }

        // Se o email já existe para outro usuário, não pode usar
        if (emailJaExiste) {
            throw new EmailJaExisteException(email.getValue());
        }
    }

    /**
     * Cria um novo usuário com validações de domínio
     */
    public Usuario criarUsuario(String nome, Email email, String senhaHash,
                               boolean emailJaExiste) {
        // Validar email único
        validarEmailUnico(email, null, emailJaExiste);

        // Criar usuário
        return new Usuario(nome, email, senhaHash);
    }

    /**
     * Atualiza dados de um usuário existente
     */
    public void atualizarUsuario(Usuario usuario, String novoNome, Email novoEmail,
                                boolean emailJaExiste) {
        // Validar email se foi alterado
        if (novoEmail != null && !usuario.getEmail().equals(novoEmail)) {
            validarEmailUnico(novoEmail, usuario, emailJaExiste);
            usuario.atualizarEmail(novoEmail);
        }

        // Atualizar nome se foi informado
        if (novoNome != null && !novoNome.trim().isEmpty()) {
            usuario.atualizarNome(novoNome);
        }
    }
}
```

---

## Parte 4: Application Layer (Ports e Use Cases)

### 4.1 Input Ports (Casos de Uso)

#### CriarUsuarioUseCase.java

```java
package com.exemplo.hexagonal.application.port.input;

import com.exemplo.hexagonal.application.dto.CreateUsuarioCommand;
import com.exemplo.hexagonal.application.dto.UsuarioDTO;

/**
 * Port para o caso de uso de criação de usuário
 */
public interface CriarUsuarioUseCase {
    UsuarioDTO criarUsuario(CreateUsuarioCommand command);
}
```

#### BuscarUsuarioUseCase.java

```java
package com.exemplo.hexagonal.application.port.input;

import com.exemplo.hexagonal.application.dto.UsuarioDTO;

import java.util.List;

/**
 * Port para casos de uso de busca de usuários
 */
public interface BuscarUsuarioUseCase {
    UsuarioDTO buscarPorId(Long id);
    UsuarioDTO buscarPorEmail(String email);
    List<UsuarioDTO> listarTodos();
    List<UsuarioDTO> buscarPorNome(String nome);
}
```

#### AtualizarUsuarioUseCase.java

```java
package com.exemplo.hexagonal.application.port.input;

import com.exemplo.hexagonal.application.dto.UpdateUsuarioCommand;
import com.exemplo.hexagonal.application.dto.UsuarioDTO;

/**
 * Port para o caso de uso de atualização de usuário
 */
public interface AtualizarUsuarioUseCase {
    UsuarioDTO atualizarUsuario(Long id, UpdateUsuarioCommand command);
}
```

#### InativarUsuarioUseCase.java

```java
package com.exemplo.hexagonal.application.port.input;

import com.exemplo.hexagonal.application.dto.UsuarioDTO;

/**
 * Port para casos de uso de inativação/ativação de usuários
 */
public interface InativarUsuarioUseCase {
    void inativarUsuario(Long id);
    UsuarioDTO reativarUsuario(Long id);
}
```

### 4.2 Output Ports (Dependências Externas)

#### UsuarioRepositoryPort.java

```java
package com.exemplo.hexagonal.application.port.output;

import com.exemplo.hexagonal.domain.model.Email;
import com.exemplo.hexagonal.domain.model.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Port para persistência de usuários
 */
public interface UsuarioRepositoryPort {

    Usuario salvar(Usuario usuario);

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorEmail(Email email);

    List<Usuario> buscarTodos();

    List<Usuario> buscarPorNome(String nome);

    boolean existePorEmail(Email email);

    boolean existePorEmailEIdDiferente(Email email, Long id);

    void deletar(Long id);
}
```

#### PasswordEncoderPort.java

```java
package com.exemplo.hexagonal.application.port.output;

/**
 * Port para criptografia de senhas
 */
public interface PasswordEncoderPort {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
```

#### EmailServicePort.java

```java
package com.exemplo.hexagonal.application.port.output;

import com.exemplo.hexagonal.domain.model.Email;

/**
 * Port para serviços de email (opcional - para futuras funcionalidades)
 */
public interface EmailServicePort {
    void enviarEmailBoasVindas(Email email, String nome);
    void enviarEmailConfirmacao(Email email, String token);
}
```

### 4.3 DTOs da Application

#### UsuarioDTO.java

```java
package com.exemplo.hexagonal.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class UsuarioDTO {

    private Long id;
    private String nome;
    private String email;
    private Boolean ativo;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataCriacao;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataAtualizacao;

    // Construtores
    public UsuarioDTO() {}

    public UsuarioDTO(Long id, String nome, String email, Boolean ativo,
                     LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.ativo = ativo;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}
```

#### CreateUsuarioCommand.java

```java
package com.exemplo.hexagonal.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Command para criação de usuário
 */
public class CreateUsuarioCommand {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    private String senha;

    // Construtores
    public CreateUsuarioCommand() {}

    public CreateUsuarioCommand(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}
```

#### UpdateUsuarioCommand.java

```java
package com.exemplo.hexagonal.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * Command para atualização de usuário
 */
public class UpdateUsuarioCommand {

    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @Email(message = "Email deve ter formato válido")
    private String email;

    // Construtores
    public UpdateUsuarioCommand() {}

    public UpdateUsuarioCommand(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
```

### 4.4 Implementação dos Use Cases

#### CriarUsuarioService.java

```java
package com.exemplo.hexagonal.application.service;

import com.exemplo.hexagonal.application.dto.CreateUsuarioCommand;
import com.exemplo.hexagonal.application.dto.UsuarioDTO;
import com.exemplo.hexagonal.application.port.input.CriarUsuarioUseCase;
import com.exemplo.hexagonal.application.port.output.PasswordEncoderPort;
import com.exemplo.hexagonal.application.port.output.UsuarioRepositoryPort;
import com.exemplo.hexagonal.domain.model.Email;
import com.exemplo.hexagonal.domain.model.Usuario;
import com.exemplo.hexagonal.domain.service.UsuarioDomainService;
import com.exemplo.hexagonal.infrastructure.mapper.UsuarioMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper entre Domain Model e JPA Entities
 */
@Component
public class UsuarioEntityMapper {

    public UsuarioEntity toEntity(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        UsuarioEntity entity = new UsuarioEntity(
            usuario.getNome(),
            usuario.getEmail().getValue(),
            usuario.getSenhaHash(),
            usuario.getAtivo(),
            usuario.getDataCriacao(),
            usuario.getDataAtualizacao()
        );

        // Se o usuário já tem ID (não é novo), definir no entity
        if (!usuario.isNovo()) {
            entity.setId(usuario.getId());
        }

        return entity;
    }

    public Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) {
            return null;
        }

        Email email = new Email(entity.getEmail());

        // Usar construtor de reconstrução
        Usuario usuario = new Usuario(
            entity.getId(),
            entity.getNome(),
            email,
            entity.getSenhaHash(),
            entity.getAtivo(),
            entity.getDataCriacao(),
            entity.getDataAtualizacao()
        );

        return usuario;
    }

    public List<Usuario> toDomainList(List<UsuarioEntity> entities) {
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
}
```

### 5.4 Exception Handler

#### GlobalExceptionHandler.java

```java
package com.exemplo.hexagonal.infrastructure.adapter.input.web;

import com.exemplo.hexagonal.domain.exception.DomainException;
import com.exemplo.hexagonal.domain.exception.EmailJaExisteException;
import com.exemplo.hexagonal.domain.exception.UsuarioNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler global para tratamento de exceções
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioNotFound(UsuarioNotFoundException ex) {
        Map<String, Object> error = createErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Usuário não encontrado",
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(EmailJaExisteException.class)
    public ResponseEntity<Map<String, Object>> handleEmailJaExiste(EmailJaExisteException ex) {
        Map<String, Object> error = createErrorResponse(
            HttpStatus.CONFLICT.value(),
            "Email já existe",
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String, Object>> handleDomainException(DomainException ex) {
        Map<String, Object> error = createErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Erro de domínio",
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> errorResponse = createErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Dados inválidos",
            "Verifique os campos informados"
        );
        errorResponse.put("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> error = createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Erro interno do servidor",
            "Ocorreu um erro inesperado"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private Map<String, Object> createErrorResponse(int status, String error, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status);
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        return errorResponse;
    }
}
```

### 5.5 Configuração

#### BeanConfiguration.java

```java
package com.exemplo.hexagonal.infrastructure.config;

import com.exemplo.hexagonal.domain.service.UsuarioDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração de beans da aplicação
 */
@Configuration
public class BeanConfiguration {

    /**
     * Bean para o serviço de domínio de usuário
     */
    @Bean
    public UsuarioDomainService usuarioDomainService() {
        return new UsuarioDomainService();
    }
}
```

---

## Parte 6: Execução e Comparação

### 6.1 Atualizando o pom.xml

Adicione as dependências de validação ao `pom.xml`:

```xml
<!-- Spring Boot Starter Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- Spring Security (para BCrypt) -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-crypto</artifactId>
</dependency>
```

### 6.2 Execução

```bash
# Compilar e executar
mvn clean install
mvn spring-boot:run
```

### 6.3 Testes das APIs

As APIs permanecem **exatamente iguais** ao Tutorial 1! Esta é uma das grandes vantagens da arquitetura hexagonal - a interface externa não muda.

```bash
# Todas as APIs funcionam igual ao Tutorial 1
curl -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao@email.com",
    "senha": "12345678"
  }'
```

---

## Parte 7: Comparação Detalhada

### 7.1 Diferenças Arquiteturais

| Aspecto           | 3 Camadas                         | Hexagonal                      |
| ----------------- | --------------------------------- | ------------------------------ |
| **Estrutura**     | Controller → Service → Repository | Port → UseCase → DomainService |
| **Dependências**  | Camadas acopladas a frameworks    | Core independente              |
| **Domínio**       | Anêmico (só getters/setters)      | Rico (regras + comportamentos) |
| **Validações**    | Espalhadas por camadas            | Centralizadas no domínio       |
| **Testabilidade** | Testes com mocks complexos        | Testes de domínio puros        |
| **Flexibilidade** | Limitada                          | Máxima                         |

### 7.2 Vantagens da Arquitetura Hexagonal

#### ✅ **Isolamento do Domínio**

```java
// Antes (3 camadas) - Entidade anêmica
@Entity
public class Usuario {
    private String nome;
    // Apenas getters/setters
}

// Depois (Hexagonal) - Domínio rico
public class Usuario {
    public void atualizarNome(String novoNome) {
        // Validações e regras dentro da entidade
        if (nome == null || nome.trim().isEmpty()) {
            throw new DomainException("Nome não pode ser vazio");
        }
        this.nome = novoNome.trim();
    }
}
```

#### ✅ **Independência de Framework**

```java
// Antes - Dependente do Spring/JPA
@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository repository; // Acoplado ao JPA
}

// Depois - Independente
public class CriarUsuarioService implements CriarUsuarioUseCase {
    private final UsuarioRepositoryPort repository; // Abstração
}
```

#### ✅ **Testabilidade Superior**

```java
// Teste do domínio puro (sem infraestrutura)
@Test
void deveValidarEmailAoCriarUsuario() {
    // Teste direto das regras de negócio
    assertThrows(DomainException.class, () ->
        new Email("email-inválido"));
}

// Teste do use case com mocks simples
@Test
void deveCriarUsuarioComSucesso() {
    // Mock simples da interface
    UsuarioRepositoryPort mockRepo = mock(UsuarioRepositoryPort.class);
    when(mockRepo.existePorEmail(any())).thenReturn(false);

    CriarUsuarioService service = new CriarUsuarioService(mockRepo, ...);
    // Teste isolado
}
```

#### ✅ **Flexibilidade de Adaptadores**

```java
// Múltiplos adapters para a mesma porta
public interface UsuarioRepositoryPort {
    Usuario salvar(Usuario usuario);
}

// Implementação SQLite
@Component
public class UsuarioSQLiteAdapter implements UsuarioRepositoryPort { }

// Implementação MongoDB (futuro)
@Component
public class UsuarioMongoAdapter implements UsuarioRepositoryPort { }

// Implementação em memória (testes)
public class UsuarioMemoryAdapter implements UsuarioRepositoryPort { }
```

### 7.3 Trade-offs

#### 🔴 **Complexidade Inicial**

-   Mais classes e interfaces
-   Curva de aprendizado maior
-   Setup inicial mais complexo

#### 🔴 **Over-engineering para Projetos Simples**

-   Para CRUDs simples, pode ser excessivo
-   Mais código para funcionalidades básicas

#### 🟢 **Benefícios a Longo Prazo**

-   Facilita mudanças de tecnologia
-   Testes mais rápidos e confiáveis
-   Manutenção mais simples
-   Regras de negócio centralizadas

---

## Parte 8: Quando Usar Cada Arquitetura?

### 8.1 Use 3 Camadas Quando:

-   ✅ **Projeto pequeno** com poucos requisitos
-   ✅ **Time iniciante** em arquitetura
-   ✅ **CRUD simples** sem regras complexas
-   ✅ **Prototipagem rápida**
-   ✅ **Tecnologia estável** (não vai mudar)

### 8.2 Use Hexagonal Quando:

-   ✅ **Projeto médio/grande** com evolução planejada
-   ✅ **Regras de negócio complexas**
-   ✅ **Múltiplas interfaces** (REST, GraphQL, CLI)
-   ✅ **Diferentes bancos** de dados
-   ✅ **Testes automatizados** extensivos
-   ✅ **Time experiente** em arquitetura
-   ✅ **Longo prazo** de manutenção

---

## Parte 9: Próximos Passos e Evolução

### 9.1 Extensões Possíveis

#### **Adicionar Novos Adapters**

```java
// CLI Adapter
@Component
public class UsuarioCLIAdapter {
    private final CriarUsuarioUseCase criarUsuario;

    public void executarComandoCriarUsuario(String[] args) {
        // Processar linha de comando
    }
}

// GraphQL Adapter
@Controller
public class UsuarioGraphQLController {
    private final BuscarUsuarioUseCase buscarUsuario;

    @QueryMapping
    public UsuarioDTO usuario(@Argument Long id) {
        return buscarUsuario.buscarPorId(id);
    }
}
```

#### **Trocar Banco de Dados**

```java
// Novo adapter para PostgreSQL
@Component
public class UsuarioPostgreSQLAdapter implements UsuarioRepositoryPort {
    // Implementação específica para PostgreSQL
}

// Configuração para escolher implementação
@Configuration
@Profile("postgres")
public class PostgreSQLConfig {
    @Bean
    @Primary
    public UsuarioRepositoryPort usuarioRepository(UsuarioPostgreSQLAdapter adapter) {
        return adapter;
    }
}
```

#### **Adicionar Eventos de Domínio**

```java
// No domínio
public class Usuario {
    private List<DomainEvent> events = new ArrayList<>();

    public void atualizarEmail(Email novoEmail) {
        Email emailAntigo = this.email;
        this.email = novoEmail;

        // Publicar evento
        events.add(new EmailAlteradoEvent(this.id, emailAntigo, novoEmail));
    }
}

// Event Handler
@Component
public class EmailAlteradoHandler {
    @EventHandler
    public void handle(EmailAlteradoEvent event) {
        // Enviar email de confirmação, notificar sistemas, etc.
    }
}
```

### 9.2 Métricas e Observabilidade

```java
// Decorator para métricas
@Component
public class UsuarioServiceMetricsDecorator implements CriarUsuarioUseCase {
    private final CriarUsuarioUseCase delegate;
    private final MeterRegistry meterRegistry;

    @Override
    public UsuarioDTO criarUsuario(CreateUsuarioCommand command) {
        return Timer.Sample.start(meterRegistry)
            .stop(meterRegistry.timer("usuario.criar"))
            .recordCallable(() -> delegate.criarUsuario(command));
    }
}
```

---

## Conclusão

### Resumo dos Tutoriais

**Tutorial 1 (3 Camadas):**

-   ✅ Implementação rápida e direta
-   ✅ Ideal para projetos simples
-   ✅ Fácil de entender e manter (inicialmente)
-   ❌ Acoplamento a frameworks
-   ❌ Dificuldade para testes unitários
-   ❌ Limitações para evolução

**Tutorial 2 (Hexagonal):**

-   ✅ Domínio rico e independente
-   ✅ Máxima testabilidade
-   ✅ Flexibilidade para mudanças
-   ✅ Separação clara de responsabilidades
-   ❌ Complexidade inicial maior
-   ❌ Mais código para funcionalidades simples

### Decisão Arquitetural

A escolha entre as arquiteturas deve considerar:

1. **Complexidade do domínio**
2. **Experiência do time**
3. **Tempo de vida do projeto**
4. **Requisitos de flexibilidade**
5. **Necessidade de testes**

### Migração Gradual

É possível migrar gradualmente de 3 camadas para hexagonal:

1. **Fase 1**: Criar interfaces para repositórios
2. **Fase 2**: Extrair regras para domain services
3. **Fase 3**: Criar use cases
4. **Fase 4**: Implementar ports e adapters
5. **Fase 5**: Enriquecer o modelo de domínio

**Ambas as arquiteturas têm seu lugar no desenvolvimento de software. A chave é escolher a abordagem certa para o contexto específico do seu projeto!**Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CriarUsuarioService implements CriarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final UsuarioDomainService usuarioDomainService;
    private final UsuarioMapper usuarioMapper;

    public CriarUsuarioService(UsuarioRepositoryPort usuarioRepository,
                              PasswordEncoderPort passwordEncoder,
                              UsuarioDomainService usuarioDomainService,
                              UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioDomainService = usuarioDomainService;
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    public UsuarioDTO criarUsuario(CreateUsuarioCommand command) {
        // Criar Value Objects
        Email email = new Email(command.getEmail());

        // Criptografar senha
        String senhaHash = passwordEncoder.encode(command.getSenha());

        // Verificar se email já existe
        boolean emailJaExiste = usuarioRepository.existePorEmail(email);

        // Usar domain service para criar usuário com validações
        Usuario usuario = usuarioDomainService.criarUsuario(
            command.getNome(),
            email,
            senhaHash,
            emailJaExiste
        );

        // Salvar no repositório
        Usuario usuarioSalvo = usuarioRepository.salvar(usuario);

        // Converter para DTO e retornar
        return usuarioMapper.toDTO(usuarioSalvo);
    }

}

````

#### BuscarUsuarioService.java

```java
package com.exemplo.hexagonal.application.service;

import com.exemplo.hexagonal.application.dto.UsuarioDTO;
import com.exemplo.hexagonal.application.port.input.BuscarUsuarioUseCase;
import com.exemplo.hexagonal.application.port.output.UsuarioRepositoryPort;
import com.exemplo.hexagonal.domain.exception.UsuarioNotFoundException;
import com.exemplo.hexagonal.domain.model.Email;
import com.exemplo.hexagonal.domain.model.Usuario;
import com.exemplo.hexagonal.infrastructure.mapper.UsuarioMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BuscarUsuarioService implements BuscarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public BuscarUsuarioService(UsuarioRepositoryPort usuarioRepository,
                               UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    public UsuarioDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.buscarPorId(id)
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new UsuarioNotFoundException(id));

        return usuarioMapper.toDTO(usuario);
    }

    @Override
    public UsuarioDTO buscarPorEmail(String emailString) {
        Email email = new Email(emailString);

        Usuario usuario = usuarioRepository.buscarPorEmail(email)
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new UsuarioNotFoundException("email", emailString));

        return usuarioMapper.toDTO(usuario);
    }

    @Override
    public List<UsuarioDTO> listarTodos() {
        List<Usuario> usuarios = usuarioRepository.buscarTodos();

        // Filtrar apenas usuários ativos
        List<Usuario> usuariosAtivos = usuarios.stream()
                .filter(Usuario::isAtivo)
                .toList();

        return usuarioMapper.toDTOList(usuariosAtivos);
    }

    @Override
    public List<UsuarioDTO> buscarPorNome(String nome) {
        List<Usuario> usuarios = usuarioRepository.buscarPorNome(nome);

        // Filtrar apenas usuários ativos
        List<Usuario> usuariosAtivos = usuarios.stream()
                .filter(Usuario::isAtivo)
                .toList();

        return usuarioMapper.toDTOList(usuariosAtivos);
    }
}
````

#### AtualizarUsuarioService.java

```java
package com.exemplo.hexagonal.application.service;

import com.exemplo.hexagonal.application.dto.UpdateUsuarioCommand;
import com.exemplo.hexagonal.application.dto.UsuarioDTO;
import com.exemplo.hexagonal.application.port.input.AtualizarUsuarioUseCase;
import com.exemplo.hexagonal.application.port.output.UsuarioRepositoryPort;
import com.exemplo.hexagonal.domain.exception.UsuarioNotFoundException;
import com.exemplo.hexagonal.domain.model.Email;
import com.exemplo.hexagonal.domain.model.Usuario;
import com.exemplo.hexagonal.domain.service.UsuarioDomainService;
import com.exemplo.hexagonal.infrastructure.mapper.UsuarioMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AtualizarUsuarioService implements AtualizarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final UsuarioDomainService usuarioDomainService;
    private final UsuarioMapper usuarioMapper;

    public AtualizarUsuarioService(UsuarioRepositoryPort usuarioRepository,
                                  UsuarioDomainService usuarioDomainService,
                                  UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioDomainService = usuarioDomainService;
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    public UsuarioDTO atualizarUsuario(Long id, UpdateUsuarioCommand command) {
        // Buscar usuário existente
        Usuario usuario = usuarioRepository.buscarPorId(id)
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new UsuarioNotFoundException(id));

        // Preparar dados para atualização
        String novoNome = command.getNome();
        Email novoEmail = null;
        boolean emailJaExiste = false;

        // Processar email se foi informado
        if (command.getEmail() != null && !command.getEmail().trim().isEmpty()) {
            novoEmail = new Email(command.getEmail());

            // Verificar se email já existe para outro usuário
            emailJaExiste = usuarioRepository.existePorEmailEIdDiferente(novoEmail, id);
        }

        // Usar domain service para atualizar com validações
        usuarioDomainService.atualizarUsuario(usuario, novoNome, novoEmail, emailJaExiste);

        // Salvar alterações
        Usuario usuarioAtualizado = usuarioRepository.salvar(usuario);

        return usuarioMapper.toDTO(usuarioAtualizado);
    }
}
```

#### InativarUsuarioService.java

```java
package com.exemplo.hexagonal.application.service;

import com.exemplo.hexagonal.application.dto.UsuarioDTO;
import com.exemplo.hexagonal.application.port.input.InativarUsuarioUseCase;
import com.exemplo.hexagonal.application.port.output.UsuarioRepositoryPort;
import com.exemplo.hexagonal.domain.exception.DomainException;
import com.exemplo.hexagonal.domain.exception.UsuarioNotFoundException;
import com.exemplo.hexagonal.domain.model.Usuario;
import com.exemplo.hexagonal.infrastructure.mapper.UsuarioMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InativarUsuarioService implements InativarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public InativarUsuarioService(UsuarioRepositoryPort usuarioRepository,
                                 UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    public void inativarUsuario(Long id) {
        Usuario usuario = usuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));

        if (!usuario.isAtivo()) {
            throw new DomainException("Usuário já está inativo");
        }

        // Usar método do domain model
        usuario.inativar();

        // Salvar alterações
        usuarioRepository.salvar(usuario);
    }

    @Override
    public UsuarioDTO reativarUsuario(Long id) {
        Usuario usuario = usuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));

        if (usuario.isAtivo()) {
            throw new DomainException("Usuário já está ativo");
        }

        // Usar método do domain model
        usuario.ativar();

        // Salvar alterações
        Usuario usuarioReativado = usuarioRepository.salvar(usuario);

        return usuarioMapper.toDTO(usuarioReativado);
    }
}
```

---

## Parte 5: Infrastructure Layer (Adapters)

### 5.1 Secondary Adapters (Output)

#### UsuarioEntity.java (JPA Entity)

```java
package com.exemplo.hexagonal.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    // Construtor padrão (JPA)
    public UsuarioEntity() {}

    // Construtor completo
    public UsuarioEntity(String nome, String email, String senhaHash, Boolean ativo,
                        LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.ativo = ativo;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    // Métodos de ciclo de vida JPA
    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}
```

#### UsuarioJpaRepository.java

```java
package com.exemplo.hexagonal.infrastructure.adapter.output.persistence;

import com.exemplo.hexagonal.infrastructure.adapter.output.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM UsuarioEntity u WHERE LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<UsuarioEntity> findByNomeContainingIgnoreCase(@Param("nome") String nome);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UsuarioEntity u WHERE u.email = :email AND u.id <> :id")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("id") Long id);
}
```

#### UsuarioRepositoryAdapter.java

```java
package com.exemplo.hexagonal.infrastructure.adapter.output.persistence;

import com.exemplo.hexagonal.application.port.output.UsuarioRepositoryPort;
import com.exemplo.hexagonal.domain.model.Email;
import com.exemplo.hexagonal.domain.model.Usuario;
import com.exemplo.hexagonal.infrastructure.adapter.output.persistence.entity.UsuarioEntity;
import com.exemplo.hexagonal.infrastructure.mapper.UsuarioEntityMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adapter que implementa o port de repositório usando JPA
 */
@Component
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository jpaRepository;
    private final UsuarioEntityMapper entityMapper;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository jpaRepository,
                                   UsuarioEntityMapper entityMapper) {
        this.jpaRepository = jpaRepository;
        this.entityMapper = entityMapper;
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        UsuarioEntity entity = entityMapper.toEntity(usuario);
        UsuarioEntity savedEntity = jpaRepository.save(entity);
        return entityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return jpaRepository.findById(id)
                .map(entityMapper::toDomain);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(Email email) {
        return jpaRepository.findByEmail(email.getValue())
                .map(entityMapper::toDomain);
    }

    @Override
    public List<Usuario> buscarTodos() {
        List<UsuarioEntity> entities = jpaRepository.findAll();
        return entityMapper.toDomainList(entities);
    }

    @Override
    public List<Usuario> buscarPorNome(String nome) {
        List<UsuarioEntity> entities = jpaRepository.findByNomeContainingIgnoreCase(nome);
        return entityMapper.toDomainList(entities);
    }

    @Override
    public boolean existePorEmail(Email email) {
        return jpaRepository.existsByEmail(email.getValue());
    }

    @Override
    public boolean existePorEmailEIdDiferente(Email email, Long id) {
        return jpaRepository.existsByEmailAndIdNot(email.getValue(), id);
    }

    @Override
    public void deletar(Long id) {
        jpaRepository.deleteById(id);
    }
}
```

#### BCryptPasswordEncoderAdapter.java

```java
package com.exemplo.hexagonal.infrastructure.adapter.output.encoder;

import com.exemplo.hexagonal.application.port.output.PasswordEncoderPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Adapter para criptografia de senhas usando BCrypt
 */
@Component
public class BCryptPasswordEncoderAdapter implements PasswordEncoderPort {

    private final BCryptPasswordEncoder encoder;

    public BCryptPasswordEncoderAdapter() {
        this.encoder = new BCryptPasswordEncoder();
    }

    @Override
    public String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
```

### 5.2 Primary Adapters (Input)

#### UsuarioController.java

```java
package com.exemplo.hexagonal.infrastructure.adapter.input.web;

import com.exemplo.hexagonal.application.dto.CreateUsuarioCommand;
import com.exemplo.hexagonal.application.dto.UpdateUsuarioCommand;
import com.exemplo.hexagonal.application.dto.UsuarioDTO;
import com.exemplo.hexagonal.application.port.input.AtualizarUsuarioUseCase;
import com.exemplo.hexagonal.application.port.input.BuscarUsuarioUseCase;
import com.exemplo.hexagonal.application.port.input.CriarUsuarioUseCase;
import com.exemplo.hexagonal.application.port.input.InativarUsuarioUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Primary Adapter - Controlador REST para operações de usuário
 */
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final CriarUsuarioUseCase criarUsuarioUseCase;
    private final BuscarUsuarioUseCase buscarUsuarioUseCase;
    private final AtualizarUsuarioUseCase atualizarUsuarioUseCase;
    private final InativarUsuarioUseCase inativarUsuarioUseCase;

    public UsuarioController(CriarUsuarioUseCase criarUsuarioUseCase,
                            BuscarUsuarioUseCase buscarUsuarioUseCase,
                            AtualizarUsuarioUseCase atualizarUsuarioUseCase,
                            InativarUsuarioUseCase inativarUsuarioUseCase) {
        this.criarUsuarioUseCase = criarUsuarioUseCase;
        this.buscarUsuarioUseCase = buscarUsuarioUseCase;
        this.atualizarUsuarioUseCase = atualizarUsuarioUseCase;
        this.inativarUsuarioUseCase = inativarUsuarioUseCase;
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> criarUsuario(@Valid @RequestBody CreateUsuarioCommand command) {
        UsuarioDTO usuario = criarUsuarioUseCase.criarUsuario(command);
        URI location = URI.create("/api/usuarios/" + usuario.getId());
        return ResponseEntity.created(location).body(usuario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        UsuarioDTO usuario = buscarUsuarioUseCase.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarTodos() {
        List<UsuarioDTO> usuarios = buscarUsuarioUseCase.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<UsuarioDTO>> buscarPorNome(@RequestParam String nome) {
        List<UsuarioDTO> usuarios = buscarUsuarioUseCase.buscarPorNome(nome);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioDTO> buscarPorEmail(@PathVariable String email) {
        UsuarioDTO usuario = buscarUsuarioUseCase.buscarPorEmail(email);
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizarUsuario(@PathVariable Long id,
                                                      @Valid @RequestBody UpdateUsuarioCommand command) {
        UsuarioDTO usuario = atualizarUsuarioUseCase.atualizarUsuario(id, command);
        return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativarUsuario(@PathVariable Long id) {
        inativarUsuarioUseCase.inativarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reativar")
    public ResponseEntity<UsuarioDTO> reativarUsuario(@PathVariable Long id) {
        UsuarioDTO usuario = inativarUsuarioUseCase.reativarUsuario(id);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Sistema CRUD Hexagonal funcionando!");
    }
}
```

### 5.3 Mappers

#### UsuarioMapper.java (Domain ↔ DTO)

```java
package com.exemplo.hexagonal.infrastructure.mapper;

import com.exemplo.hexagonal.application.dto.UsuarioDTO;
import com.exemplo.hexagonal.domain.model.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper entre Domain Model e DTOs
 */
@Component
public class UsuarioMapper {

    public UsuarioDTO toDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        return new UsuarioDTO(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail().getValue(),
            usuario.getAtivo(),
            usuario.getDataCriacao(),
            usuario.getDataAtualizacao()
        );
    }

    public List<UsuarioDTO> toDTOList(List<Usuario> usuarios) {
        return usuarios.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
```

#### UsuarioEntityMapper.java (Domain ↔ Entity)

```java
package com.exemplo.hexagonal.infrastructure.mapper;

import com.exemplo.hexagonal.domain.model.Email;
import com.exemplo.hexagonal.domain.model.Usuario;
import com.exemplo.hexagonal.infrastructure.adapter.output.persistence.entity.UsuarioEntity;
import org.springframework.stereotype.
```
