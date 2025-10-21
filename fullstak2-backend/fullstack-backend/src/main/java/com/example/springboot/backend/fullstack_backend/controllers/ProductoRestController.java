package com.example.springboot.backend.fullstack_backend.controllers;

import java.util.List;
import java.util.Map;
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

import com.example.springboot.backend.fullstack_backend.entity.Producto;
import com.example.springboot.backend.fullstack_backend.service.ProductoService;
import jakarta.validation.Valid;


@RestController
@RequestMapping(value = "api/productos", produces = "application/json;charset=UTF-8")
public class ProductoRestController {

    @Autowired
    private ProductoService productoservice;

    @GetMapping
    public List<Producto> mostrarProducto() {
        return productoservice.findByAll();
    }

    @GetMapping("/ofertas")
    public List<Producto> listarOfertas() {
        return productoservice.findOfertas();
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<?> verProducto(@PathVariable Long id) {
        Optional<Producto> optionalProducto = productoservice.findById(id);
        if (optionalProducto.isPresent()) {
            return ResponseEntity.ok(optionalProducto.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody Producto unProducto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoservice.save(unProducto));
    }

    @PutMapping("/{id}")
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        Optional<Producto> productoOptional = productoservice.findById(id);
        if (productoOptional.isPresent()) {
            productoservice.delete(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/batch")
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