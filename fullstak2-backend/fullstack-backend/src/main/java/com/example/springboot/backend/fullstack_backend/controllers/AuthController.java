package com.example.springboot.backend.fullstack_backend.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.backend.fullstack_backend.entity.Usuario;
import com.example.springboot.backend.fullstack_backend.service.UsuarioService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

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
            response.put("token", "admin-token-" + System.currentTimeMillis());
            response.put("user", user);
            return ResponseEntity.ok(response);
        }

        // Aquí podrías validar contra la base de datos
        // Por ahora retornamos un error genérico
        Map<String, String> error = new HashMap<>();
        error.put("message", "Credenciales inválidas");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario usuario) {
        try {
            // Validar si el email ya existe
            // Por ahora guardamos directamente
            Usuario nuevoUsuario = usuarioService.save(usuario);
            
            Map<String, Object> response = new HashMap<>();
            Map<String, String> user = new HashMap<>();
            user.put("email", nuevoUsuario.getCorreo());
            user.put("nombre", nuevoUsuario.getNombre());
            response.put("token", "user-token-" + System.currentTimeMillis());
            response.put("user", user);
            response.put("ok", true);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al registrar usuario");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
