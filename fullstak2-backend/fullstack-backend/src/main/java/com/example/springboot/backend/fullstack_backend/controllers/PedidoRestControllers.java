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

import com.example.springboot.backend.fullstack_backend.entity.Pedido;
import com.example.springboot.backend.fullstack_backend.service.PedidoService;

@RestController
@RequestMapping("api/pedidos")
public class PedidoRestControllers {

	@Autowired
	private PedidoService service;

	@GetMapping
	public List<Pedido> listar() {
		return service.findByAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> verDetalle(@PathVariable Long id) {
		Optional<Pedido> pedidoOptional = service.findById(id);
		if (pedidoOptional.isPresent()) {
			return ResponseEntity.ok(pedidoOptional.get());
		}
		return ResponseEntity.notFound().build();
	}

	@PostMapping
	public ResponseEntity<Pedido> crear(@RequestBody Pedido unPedido) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.save(unPedido));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> modificar(@PathVariable Long id, @RequestBody Pedido unPedido) {
		Optional<Pedido> pedidoOptional = service.findById(id);
		if (pedidoOptional.isPresent()) {
			Pedido pedidoExistente = pedidoOptional.get();
			pedidoExistente.setFecha(unPedido.getFecha());
			pedidoExistente.setTotal(unPedido.getTotal());
			pedidoExistente.setUsuario(unPedido.getUsuario());
			pedidoExistente.setDetalles(unPedido.getDetalles());
			Pedido pedidoModificado = service.save(pedidoExistente);
			return ResponseEntity.ok(pedidoModificado);
		}
		return ResponseEntity.notFound().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Long id) {
		Optional<Pedido> pedidoOptional = service.delete(id);
		if (pedidoOptional.isPresent()) {
			return ResponseEntity.ok(pedidoOptional.get());
		}
		return ResponseEntity.notFound().build();
	}
}
