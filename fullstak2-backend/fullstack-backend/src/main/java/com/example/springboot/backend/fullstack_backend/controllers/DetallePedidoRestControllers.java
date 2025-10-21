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

import com.example.springboot.backend.fullstack_backend.entity.DetallePedido;
import com.example.springboot.backend.fullstack_backend.service.DetallePedidoService;

@RestController
@RequestMapping("api/detallepedidos")
public class DetallePedidoRestControllers {

	@Autowired
	private DetallePedidoService service;

	@GetMapping
	public List<DetallePedido> listar() {
		return service.findByAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> verDetalle(@PathVariable Long id) {
		Optional<DetallePedido> optional = service.findById(id);
		if (optional.isPresent()) {
			return ResponseEntity.ok(optional.get());
		}
		return ResponseEntity.notFound().build();
	}

	@PostMapping
	public ResponseEntity<DetallePedido> crear(@RequestBody DetallePedido unDetallePedido) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.save(unDetallePedido));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> modificar(@PathVariable Long id, @RequestBody DetallePedido unDetallePedido) {
		Optional<DetallePedido> optional = service.findById(id);
		if (optional.isPresent()) {
			DetallePedido existente = optional.get();
			existente.setCantidad(unDetallePedido.getCantidad());
			existente.setSubtotal(unDetallePedido.getSubtotal());
			existente.setProducto(unDetallePedido.getProducto());
			existente.setPedido(unDetallePedido.getPedido());
			DetallePedido actualizado = service.save(existente);
			return ResponseEntity.ok(actualizado);
		}
		return ResponseEntity.notFound().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Long id) {
		Optional<DetallePedido> eliminado = service.delete(id);
		if (eliminado.isPresent()) {
			return ResponseEntity.ok(eliminado.get());
		}
		return ResponseEntity.notFound().build();
	}
}
