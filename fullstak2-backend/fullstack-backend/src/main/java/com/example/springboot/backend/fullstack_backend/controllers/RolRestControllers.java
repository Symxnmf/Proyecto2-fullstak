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

import com.example.springboot.backend.fullstack_backend.entity.Rol;
import com.example.springboot.backend.fullstack_backend.service.RolService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "Endpoints para gestión de roles")
public class RolRestControllers {

    @Autowired
    private RolService service;

    @GetMapping
    @Operation(summary = "Listar roles")
    public List<Rol> listar() {
        return service.findByAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de un rol por id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Rol encontrado"),
        @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    public ResponseEntity<Rol> verDetalle(
        @Parameter(description = "ID del rol", required = true) @PathVariable Long id) {

        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo rol")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Rol creado")
    })
    public ResponseEntity<Rol> crear(@RequestBody Rol unRol) {
        Rol creado = service.save(unRol);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar un rol existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Rol modificado"),
        @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    public ResponseEntity<Rol> modificar(
        @Parameter(description = "ID del rol a modificar", required = true) @PathVariable Long id,
        @RequestBody Rol unRol) {

        Optional<Rol> rolOptional = service.findById(id);
        if (rolOptional.isPresent()) {
            Rol rolExistente = rolOptional.get();
            rolExistente.setNombre(unRol.getNombre());
            Rol rolModificado = service.save(rolExistente);
            return ResponseEntity.ok(rolModificado);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un rol por id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Rol eliminado"),
        @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    public ResponseEntity<Rol> eliminar(
        @Parameter(description = "ID del rol a eliminar", required = true) @PathVariable Long id) {

        Rol unRol = new Rol();
        unRol.setId(id);
        return service.delete(unRol)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}