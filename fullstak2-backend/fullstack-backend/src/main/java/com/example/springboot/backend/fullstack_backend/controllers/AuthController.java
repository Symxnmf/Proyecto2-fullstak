package com.example.springboot.backend.fullstack_backend.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.backend.fullstack_backend.dto.AuthRequest;
import com.example.springboot.backend.fullstack_backend.dto.AuthResponse;
import com.example.springboot.backend.fullstack_backend.entity.Usuario;
import com.example.springboot.backend.fullstack_backend.security.JwtService;
import com.example.springboot.backend.fullstack_backend.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Autenticación y registro")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        String email = req.getEmail();
        String password = req.getPassword();

        if ("Admin@duocuc.cl".equalsIgnoreCase(email) && "Duoc12345.".equals(password)) {
            Map<String, Object> user = new HashMap<>();
            user.put("email", "Admin@duocuc.cl");
            user.put("nombre", "Administrador");
            user.put("rol", "ADMIN");

            Map<String, Object> claims = new HashMap<>();
            claims.put("role", "ADMIN");
            String access = jwtService.generateAccessToken(email, claims);
            String refresh = jwtService.generateRefreshToken(email, claims);
            return ResponseEntity.ok(new AuthResponse(access, refresh, user));
        }

        try {
            Optional<Usuario> usuarioBD = usuarioService.findByCorreo(email);
            if (usuarioBD.isPresent()) {
                Usuario user = usuarioBD.get();
                String rol = user.getRol() != null ? user.getRol() : "CLIENTE";
                // Validar password con BCrypt
                if (user.getContrasena() != null && passwordEncoder.matches(password, user.getContrasena())) {
                    Map<String, Object> claims = new HashMap<>();
                    claims.put("role", rol);
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("email", user.getCorreo());
                    userMap.put("nombre", user.getNombre());
                    userMap.put("rol", rol);
                    String access = jwtService.generateAccessToken(email, claims);
                    String refresh = jwtService.generateRefreshToken(email, claims);
                    return ResponseEntity.ok(new AuthResponse(access, refresh, userMap));
                }

                if (user.getContrasena() != null && user.getContrasena().equals(password)) {
                    user.setContrasena(passwordEncoder.encode(password));
                    usuarioService.save(user);
                    Map<String, Object> claims = new HashMap<>();
                    claims.put("role", rol);
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("email", user.getCorreo());
                    userMap.put("nombre", user.getNombre());
                    userMap.put("rol", rol);
                    String access = jwtService.generateAccessToken(email, claims);
                    String refresh = jwtService.generateRefreshToken(email, claims);
                    return ResponseEntity.ok(new AuthResponse(access, refresh, userMap));
                }
            }
        } catch (Exception e) {}

        Map<String, String> error = new HashMap<>();
        error.put("message", "Credenciales inválidas");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> datos) {
        try {
            String email = datos.get("email");
            
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
            // Hashear contraseña con BCrypt
            nuevoUsuario.setContrasena(passwordEncoder.encode(datos.get("password")));
            nuevoUsuario.setRol("CLIENTE"); // Todos los registros nuevos son clientes
            
            // Persistir en base de datos
            Usuario saved = usuarioService.save(nuevoUsuario);
            
            // Construir respuesta exitosa con datos del usuario
            Map<String, Object> user = new HashMap<>();
            user.put("email", saved.getCorreo());
            user.put("nombre", saved.getNombre());
            user.put("rol", "CLIENTE");

            Map<String, Object> claims = new HashMap<>();
            claims.put("role", "CLIENTE");
            String access = jwtService.generateAccessToken(saved.getCorreo(), claims);
            String refresh = jwtService.generateRefreshToken(saved.getCorreo(), claims);
            AuthResponse response = new AuthResponse(access, refresh, user);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al registrar usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "refreshToken es requerido"));
        }
        try {
            String email = jwtService.extractUsername(refreshToken);
            if (!jwtService.isTokenValid(refreshToken, email)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "refreshToken inválido"));
            }
            String rol = jwtService.extractAllClaims(refreshToken).get("role", String.class);
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", rol);
            String newAccess = jwtService.generateAccessToken(email, claims);
            return ResponseEntity.ok(Map.of("accessToken", newAccess));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "refreshToken inválido"));
        }
    }
}
