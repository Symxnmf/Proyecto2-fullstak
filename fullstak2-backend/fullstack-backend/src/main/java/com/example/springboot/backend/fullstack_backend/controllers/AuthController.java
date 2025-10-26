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

    // Inyección de dependencia del servicio de usuarios
    @Autowired
    private UsuarioService usuarioService;

    @Operation(summary = "Login de usuario", description = "Valida credenciales y devuelve token + datos básicos")
    @ApiResponse(responseCode = "200", description = "Login correcto", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    /**
     * Endpoint para inicio de sesión
     * Valida credenciales contra admin hardcodeado o usuarios en BD
     * @param credentials Map con email y password
     * @return ResponseEntity con token y datos del usuario o error 401
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        // Extraer credenciales del body
        String email = credentials.get("email");
        String password = credentials.get("password");

        // Primero validamos si es el administrador del sistema
        // TODO: En producción esto debería estar en BD también
        if ("Admin@duocuc.cl".equalsIgnoreCase(email) && "Duoc12345.".equals(password)) {
            // Construir respuesta para admin
            Map<String, Object> response = new HashMap<>();
            Map<String, String> user = new HashMap<>();
            user.put("email", "Admin@duocuc.cl");
            user.put("nombre", "Administrador");
            user.put("rol", "ADMIN"); // Rol de administrador
            // Generamos un token simple 
            response.put("token", "admin-token-" + System.currentTimeMillis());
            response.put("user", user);
            return ResponseEntity.ok(response);
        }

        // Si no es admin, buscar en base de datos
        try {
            // Buscar usuario por correo
            Optional<Usuario> usuarioBD = usuarioService.findByCorreo(email);
            // Validar que existe y la contraseña coincide
            if (usuarioBD.isPresent() && password.equals(usuarioBD.get().getContrasena())) {
                Usuario user = usuarioBD.get();
                // Preparar respuesta exitosa
                Map<String, Object> response = new HashMap<>();
                Map<String, String> userData = new HashMap<>();
                userData.put("email", user.getCorreo());
                userData.put("nombre", user.getNombre());
                // Asignar rol, por defecto CLIENTE si no tiene
                userData.put("rol", user.getRol() != null ? user.getRol() : "CLIENTE");
                response.put("token", "user-token-" + System.currentTimeMillis());
                response.put("user", userData);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            // Si hay error en la búsqueda, continuar al mensaje de error genérico
            // No exponemos si el usuario existe o no por seguridad
        }

        // Si llegamos aquí, las credenciales son inválidas
        Map<String, String> error = new HashMap<>();
        error.put("message", "Credenciales inválidas");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @Operation(summary = "Registro de usuario", description = "Crea un nuevo usuario y retorna token + datos básicos")
    @ApiResponse(responseCode = "201", description = "Usuario registrado", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Error en el registro")
    /**
     * Endpoint para registro de nuevos usuarios
     * Crea un usuario con rol CLIENTE por defecto
     * @param datos Map con nombre, email y password
     * @return ResponseEntity con usuario creado y token, o error
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> datos) {
        try {
            // Extraer email del request
            String email = datos.get("email");
            
            // Validar que el email no esté ya registrado
            Optional<Usuario> existente = usuarioService.findByCorreo(email);
            if (existente.isPresent()) {
                // Email duplicado, retornar error
                Map<String, String> error = new HashMap<>();
                error.put("message", "El email ya está registrado");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            
            // Crear nueva instancia de Usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(datos.get("nombre"));
            nuevoUsuario.setCorreo(email);
            nuevoUsuario.setContrasena(datos.get("password")); 
            nuevoUsuario.setRol("CLIENTE"); // Todos los registros nuevos son clientes
            
            // Persistir en base de datos
            Usuario saved = usuarioService.save(nuevoUsuario);
            
            // Construir respuesta exitosa con datos del usuario
            Map<String, Object> response = new HashMap<>();
            Map<String, String> user = new HashMap<>();
            user.put("email", saved.getCorreo());
            user.put("nombre", saved.getNombre());
            user.put("rol", "CLIENTE");
            response.put("token", "user-token-" + System.currentTimeMillis());
            response.put("user", user);
            response.put("ok", true);
            
            // Retornar 201 Created
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            // Capturar cualquier error y retornar mensaje genérico
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al registrar usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
