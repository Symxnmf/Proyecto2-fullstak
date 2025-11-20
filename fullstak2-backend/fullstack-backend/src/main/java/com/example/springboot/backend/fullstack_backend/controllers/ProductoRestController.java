package com.example.springboot.backend.fullstack_backend.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.backend.fullstack_backend.entity.Producto;
import com.example.springboot.backend.fullstack_backend.service.ProductoService;
import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "api/productos", produces = "application/json;charset=UTF-8")
@Tag(name = "Producto", description = "Operaciones relacionadas con productos")
public class ProductoRestController {

    @Autowired
    private ProductoService productoservice;

    @Operation(
        summary = "Obtener lista de productos",
        description = "Devuelve todos los registros de productos disponibles"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista de productos obtenida correctamente",
        content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Producto.class)))
    )
    @GetMapping
    public List<Producto> mostrarProducto() {
        return productoservice.findByAll();
    }

    @Operation(
        summary = "Obtener productos en oferta",
        description = "Devuelve todos los productos marcados con oferta = true"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista de productos en oferta",
        content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Producto.class)))
    )
    @GetMapping("/ofertas")
    public List<Producto> listarOfertas() {
        return productoservice.findOfertas();
    }

    @Operation(
        summary = "Contar productos por categoría",
        description = "Devuelve pares nombre/cantidad con la cantidad de productos por categoría"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Listado de conteo por categoría",
        content = @Content(mediaType = "application/json")
    )
    @GetMapping("/categorias")
    public List<Map<String, Object>> contarCategorias() {
        List<Object[]> rows = productoservice.contarPorCategoria();
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (Object[] r : rows) {
            Map<String, Object> m = new java.util.HashMap<>();
            m.put("nombre", r[0]);
            m.put("cantidad", ((Number) r[1]).intValue());
            result.add(m);
        }
        return result;
    }

    @Operation(
        summary = "Buscar productos por nombre",
        description = "Busca productos que contengan el texto en su nombre (case insensitive)"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista de productos que coinciden con la búsqueda",
        content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Producto.class)))
    )
    @GetMapping("/buscar")
    public List<Producto> buscarProductos(@RequestParam String nombre) {
        return productoservice.buscarPorNombre(nombre);
    }

    @Operation(
        summary = "Obtener productos con stock bajo",
        description = "Devuelve productos con stock menor o igual a 5 unidades"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista de productos con stock crítico",
        content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Producto.class)))
    )
    @GetMapping("/stock-bajo")
    public List<Producto> listarStockBajo() {
        return productoservice.findByAll().stream()
            .filter(p -> p.getStock() <= 5)
            .collect(java.util.stream.Collectors.toList());
    }

    @Operation(
        summary = "Obtener producto por ID",
        description = "Obtiene el detalle de un producto específico"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Producto encontrado",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Producto.class))
    )
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<?> verProducto(@PathVariable Long id) {
        Optional<Producto> optionalProducto = productoservice.findById(id);
        if (optionalProducto.isPresent()) {
            return ResponseEntity.ok(optionalProducto.get());
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(
        summary = "Crear un nuevo producto",
        description = "Crea un registro de producto con los datos proporcionados"
    )
    @ApiResponse(
        responseCode = "201",
        description = "Producto creado correctamente",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Producto.class))
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody Producto unProducto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoservice.save(unProducto));
    }

    @Operation(
        summary = "Modificar un producto existente",
        description = "Actualiza los datos de un producto si existe"
    )
    @ApiResponse(responseCode = "200", description = "Producto modificado correctamente",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Producto.class)))
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> modificarProducto(@PathVariable Long id, @Valid @RequestBody Producto unProducto) {
        Optional<Producto> optionalProducto = productoservice.findById(id);
        if (optionalProducto.isPresent()) {
            Producto productoExiste = optionalProducto.get();
            productoExiste.setNombre(unProducto.getNombre());
            productoExiste.setPrecio(unProducto.getPrecio());
            productoExiste.setStock(unProducto.getStock());
            productoExiste.setDescripcion(unProducto.getDescripcion());
            productoExiste.setCategoria(unProducto.getCategoria());
            productoExiste.setImagen(unProducto.getImagen());
            productoExiste.setOferta(unProducto.getOferta());
            Producto productoModificado = productoservice.save(productoExiste);
            return ResponseEntity.ok(productoModificado);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(
        summary = "Eliminar un producto por ID",
        description = "Elimina un producto específico del sistema"
    )
    @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        Optional<Producto> productoOptional = productoservice.findById(id);
        if (productoOptional.isPresent()) {
            productoservice.delete(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(
        summary = "Crear múltiples productos",
        description = "Crea varios productos en una sola llamada"
    )
    @ApiResponse(responseCode = "201", description = "Productos creados correctamente",
        content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Producto.class))))
    @ApiResponse(responseCode = "400", description = "Error en la creación por datos inválidos")
    @PostMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> crearProductosEnBatch(@RequestBody List<Producto> productos) {
        try {
            List<Producto> guardados = new java.util.ArrayList<>();
            for (Producto producto : productos) {
                guardados.add(productoservice.save(producto));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(guardados);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al crear productos: " + e.getMessage());
        }
    }
}