package com.example.springboot.backend.fullstack_backend.controllers;

import com.example.springboot.backend.fullstack_backend.entity.CarritoItem;
import com.example.springboot.backend.fullstack_backend.entity.Producto;
import com.example.springboot.backend.fullstack_backend.repository.CarritoItemRepository;
import com.example.springboot.backend.fullstack_backend.repository.ProductoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/carrito")
@Tag(name = "Carrito", description = "Carrito de compra persistente por usuario")
public class CarritoController {

    @Autowired
    private CarritoItemRepository carritoRepo;

    @Autowired
    private ProductoRepository productoRepo;

    @Operation(summary = "Listar carrito del usuario")
    @ApiResponse(responseCode = "200", description = "Items del carrito",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CarritoItem.class))))
    @GetMapping
    public List<CarritoItem> listar(Authentication auth) {
        return carritoRepo.findByUsuarioIgnoreCase(auth.getName());
    }

    @Operation(summary = "Agregar item al carrito")
    @ApiResponse(responseCode = "201", description = "Item agregado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarritoItem.class)))
    @PostMapping
    public ResponseEntity<?> agregar(Authentication auth, @RequestBody Map<String, Object> body) {
        try {
            Long productoId = Long.valueOf(String.valueOf(body.get("productoId")));
            int cantidad = Integer.parseInt(String.valueOf(body.getOrDefault("cantidad", 1)));
            Optional<Producto> prod = productoRepo.findById(productoId);
            if (prod.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Producto inexistente"));
            CarritoItem ci = new CarritoItem();
            ci.setUsuario(auth.getName());
            ci.setProducto(prod.get());
            ci.setCantidad(Math.max(cantidad, 1));
            return ResponseEntity.status(HttpStatus.CREATED).body(carritoRepo.save(ci));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @Operation(summary = "Actualizar cantidad de un item")
    @ApiResponse(responseCode = "200", description = "Item actualizado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarritoItem.class)))
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(Authentication auth, @PathVariable Long id, @RequestBody Map<String, Object> body) {
        Optional<CarritoItem> opt = carritoRepo.findById(id);
        if (opt.isEmpty() || !opt.get().getUsuario().equalsIgnoreCase(auth.getName())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        int cantidad = Integer.parseInt(String.valueOf(body.getOrDefault("cantidad", 1)));
        CarritoItem ci = opt.get();
        ci.setCantidad(Math.max(cantidad, 1));
        return ResponseEntity.ok(carritoRepo.save(ci));
    }

    @Operation(summary = "Eliminar item del carrito")
    @ApiResponse(responseCode = "204", description = "Item eliminado")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(Authentication auth, @PathVariable Long id) {
        Optional<CarritoItem> opt = carritoRepo.findById(id);
        if (opt.isEmpty() || !opt.get().getUsuario().equalsIgnoreCase(auth.getName())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        carritoRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Vaciar carrito del usuario")
    @ApiResponse(responseCode = "204", description = "Carrito vaciado")
    @DeleteMapping
    public ResponseEntity<?> vaciar(Authentication auth) {
        carritoRepo.deleteByUsuarioIgnoreCase(auth.getName());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Sincronizar carrito (reemplaza todo)")
    @ApiResponse(responseCode = "204", description = "Carrito sincronizado")
    @PostMapping("/sync")
    public ResponseEntity<?> sync(Authentication auth, @RequestBody Map<String, Object> body) {
        Object arr = body.get("items");
        List<?> items = (arr instanceof List) ? (List<?>) arr : List.of();
        // Vaciar actual
        carritoRepo.deleteByUsuarioIgnoreCase(auth.getName());
        // Agrupar por productoId y sumar cantidades
    Map<Long, Integer> merged = items.stream()
        .filter(o -> o instanceof Map)
        .map(o -> (Map<?, ?>) o)
        .collect(Collectors.toMap(
            m -> Long.valueOf(String.valueOf(m.get("productoId"))),
            m -> {
                Object q = m.get("cantidad");
                return q == null ? 1 : Integer.parseInt(String.valueOf(q));
            },
            Integer::sum
        ));
        for (Map.Entry<Long, Integer> e : merged.entrySet()) {
            Optional<Producto> prod = productoRepo.findById(e.getKey());
            if (prod.isEmpty()) continue;
            CarritoItem ci = new CarritoItem();
            ci.setUsuario(auth.getName());
            ci.setProducto(prod.get());
            ci.setCantidad(Math.max(e.getValue(), 1));
            carritoRepo.save(ci);
        }
        return ResponseEntity.noContent().build();
    }
}
