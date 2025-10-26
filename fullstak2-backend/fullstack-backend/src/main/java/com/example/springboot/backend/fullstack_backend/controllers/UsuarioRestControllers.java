package com.example.springboot.backend.fullstack_backend.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.backend.fullstack_backend.entity.Usuario;
import com.example.springboot.backend.fullstack_backend.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/usuarios")
@Tag(name = "Usuario", description = "Operaciones relacionadas con usuarios")
public class UsuarioRestControllers {
    
    @Autowired
    private UsuarioService service;

    @Operation(
        summary = "Obtener lista de usuarios",
        description = "Devuelve todos los registros de usuarios"
    )
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida",
        content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Usuario.class))))
    @GetMapping
    public List<Usuario> listar() {
        return service.findByAll();
    }

    @Operation(summary = "Obtener usuario por ID", description = "Obtiene el detalle de un usuario específico")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class)))
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<?> verDetalle(@PathVariable Long id) {
        Optional<Usuario> usuarioOptional = service.findById(id);
        if (usuarioOptional.isPresent()) {
            return ResponseEntity.ok(usuarioOptional.orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Crear un nuevo usuario", description = "Crea un registro de usuario con los datos proporcionados")
    @ApiResponse(responseCode = "201", description = "Usuario creado correctamente",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class)))
    @PostMapping
    public ResponseEntity<Usuario> crear(@RequestBody Usuario unUsuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(unUsuario));
    }

    @Operation(summary = "Modificar un usuario existente", description = "Actualiza los datos de un usuario si existe")
    @ApiResponse(responseCode = "200", description = "Usuario modificado correctamente",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class)))
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @PutMapping("/{id}")
    public ResponseEntity<?> modificar(@PathVariable Long id, @RequestBody Usuario unUsuario) {
        Optional<Usuario> usuarioOptional = service.findById(id);
        if (usuarioOptional.isPresent()) {
            Usuario usuarioExistente = usuarioOptional.get();
            usuarioExistente.setNombre(unUsuario.getNombre());
            usuarioExistente.setApellido(unUsuario.getApellido());
            usuarioExistente.setCorreo(unUsuario.getCorreo());
            usuarioExistente.setCelular(unUsuario.getCelular());
            Usuario usuarioModificado = service.save(usuarioExistente);
            return ResponseEntity.ok(usuarioModificado);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar un usuario por ID", description = "Elimina un usuario específico del sistema")
    @ApiResponse(responseCode = "200", description = "Usuario eliminado correctamente",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class)))
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Usuario unUsuario = new Usuario();
        unUsuario.setId(id);
        Optional<Usuario> usuarioOptional = service.delete(unUsuario);
        if (usuarioOptional.isPresent()) {
            return ResponseEntity.ok(usuarioOptional.orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }
}
