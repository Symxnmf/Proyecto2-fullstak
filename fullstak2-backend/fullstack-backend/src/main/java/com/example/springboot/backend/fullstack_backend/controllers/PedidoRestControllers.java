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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/pedidos")
@Tag(name = "Pedido", description = "Operaciones relacionadas con pedidos")
public class PedidoRestControllers {

	@Autowired
	private PedidoService service;

	@Operation(summary = "Obtener lista de pedidos", description = "Devuelve todos los registros de pedidos")
	@ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida",
		content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Pedido.class))))
	@GetMapping
	public List<Pedido> listar() {
		return service.findByAll();
	}

	@Operation(summary = "Obtener pedido por ID", description = "Obtiene el detalle de un pedido específico")
	@ApiResponse(responseCode = "200", description = "Pedido encontrado",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = Pedido.class)))
	@ApiResponse(responseCode = "404", description = "Pedido no encontrado")
	@GetMapping("/{id}")
	public ResponseEntity<?> verDetalle(@PathVariable Long id) {
		Optional<Pedido> pedidoOptional = service.findById(id);
		if (pedidoOptional.isPresent()) {
			return ResponseEntity.ok(pedidoOptional.get());
		}
		return ResponseEntity.notFound().build();
	}

	@Operation(summary = "Crear un nuevo pedido", description = "Crea un registro de pedido con los datos proporcionados")
	@ApiResponse(responseCode = "201", description = "Pedido creado correctamente",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = Pedido.class)))
	@PostMapping
	public ResponseEntity<Pedido> crear(@RequestBody Pedido unPedido) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.save(unPedido));
	}

	@Operation(summary = "Modificar un pedido existente", description = "Actualiza los datos de un pedido si existe")
	@ApiResponse(responseCode = "200", description = "Pedido modificado correctamente",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = Pedido.class)))
	@ApiResponse(responseCode = "404", description = "Pedido no encontrado")
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

	@Operation(summary = "Eliminar un pedido por ID", description = "Elimina un pedido específico del sistema")
	@ApiResponse(responseCode = "200", description = "Pedido eliminado correctamente",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = Pedido.class)))
	@ApiResponse(responseCode = "404", description = "Pedido no encontrado")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Long id) {
		Optional<Pedido> pedidoOptional = service.delete(id);
		if (pedidoOptional.isPresent()) {
			return ResponseEntity.ok(pedidoOptional.get());
		}
		return ResponseEntity.notFound().build();
	}
}
