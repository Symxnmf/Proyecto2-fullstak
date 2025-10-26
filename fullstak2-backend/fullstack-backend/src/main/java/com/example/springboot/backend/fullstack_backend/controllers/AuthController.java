package com.example.springboot.backend.fullstack_backend.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.backend.fullstack_backend.entity.Usuario;
import com.example.springboot.backend.fullstack_backend.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Endpoints de autenticación y registro")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Operation(summary = "Login de usuario", description = "Valida credenciales y devuelve token + datos básicos")
    @ApiResponse(responseCode = "200", description = "Login correcto", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        // Validación básica del admin
        if ("Admin@duocuc.cl".equalsIgnoreCase(email) && "Duoc12345.".equals(password)) {
            Map<String, Object> response = new HashMap<>();
            Map<String, String> user = new HashMap<>();
            user.put("email", "Admin@duocuc.cl");
            user.put("nombre", "Administrador");
            user.put("rol", "ADMIN");
            response.put("token", "admin-token-" + System.currentTimeMillis());
            response.put("user", user);
            return ResponseEntity.ok(response);
        }

        // Buscar usuario en la base de datos
        try {
            Optional<Usuario> usuarioBD = usuarioService.findByCorreo(email);
            if (usuarioBD.isPresent() && password.equals(usuarioBD.get().getContrasena())) {
                Usuario user = usuarioBD.get();
                Map<String, Object> response = new HashMap<>();
                Map<String, String> userData = new HashMap<>();
                userData.put("email", user.getCorreo());
                userData.put("nombre", user.getNombre());
                userData.put("rol", user.getRol() != null ? user.getRol() : "CLIENTE");
                response.put("token", "user-token-" + System.currentTimeMillis());
                response.put("user", userData);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            // Usuario no encontrado, continuar al error de credenciales
        }

        // Credenciales inválidas
        Map<String, String> error = new HashMap<>();
        error.put("message", "Credenciales inválidas");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @Operation(summary = "Registro de usuario", description = "Crea un nuevo usuario y retorna token + datos básicos")
    @ApiResponse(responseCode = "201", description = "Usuario registrado", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Error en el registro")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> datos) {
        try {
            // Validar si el email ya existe
            String email = datos.get("email");
            Optional<Usuario> existente = usuarioService.findByCorreo(email);
            if (existente.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "El email ya está registrado");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            
            // Crear nuevo usuario (siempre como CLIENTE)
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(datos.get("nombre"));
            nuevoUsuario.setCorreo(email);
            nuevoUsuario.setContrasena(datos.get("password"));
            nuevoUsuario.setRol("CLIENTE"); // Los usuarios registrados son clientes
            
            Usuario saved = usuarioService.save(nuevoUsuario);
            
            Map<String, Object> response = new HashMap<>();
            Map<String, String> user = new HashMap<>();
            user.put("email", saved.getCorreo());
            user.put("nombre", saved.getNombre());
            user.put("rol", "CLIENTE");
            response.put("token", "user-token-" + System.currentTimeMillis());
            response.put("user", user);
            response.put("ok", true);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al registrar usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
