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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/detallepedidos")
@Tag(name = "DetallePedido", description = "Operaciones relacionadas con los detalles de pedido")
public class DetallePedidoRestControllers {

	@Autowired
	private DetallePedidoService service;

	@Operation(
		summary = "Obtener lista de detalles de pedido",
		description = "Devuelve todos los registros de detalles de pedido disponibles"
	)
	@ApiResponse(
		responseCode = "200",
		description = "Lista de detalles de pedido obtenida correctamente",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = DetallePedido.class))
	)
	@GetMapping
	public List<DetallePedido> listar() {
		return service.findByAll();
	}

	@Operation(
		summary = "Obtener detalle de pedido por ID",
		description = "Obtiene el detalle de un ítem de pedido específico"
	)
	@ApiResponse(
		responseCode = "200",
		description = "Detalle de pedido encontrado",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = DetallePedido.class))
	)
	@ApiResponse(responseCode = "404", description = "Detalle de pedido no encontrado")
	@GetMapping("/{id}")
	public ResponseEntity<?> verDetalle(@PathVariable Long id) {
		Optional<DetallePedido> optional = service.findById(id);
		if (optional.isPresent()) {
			return ResponseEntity.ok(optional.get());
		}
		return ResponseEntity.notFound().build();
	}

	@Operation(
		summary = "Crear un nuevo detalle de pedido",
		description = "Crea un registro de detalle de pedido con los datos proporcionados"
	)
	@ApiResponse(
		responseCode = "201",
		description = "Detalle de pedido creado correctamente",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = DetallePedido.class))
	)
	@PostMapping
	public ResponseEntity<DetallePedido> crear(@RequestBody DetallePedido unDetallePedido) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.save(unDetallePedido));
	}

	@Operation(
		summary = "Modificar un detalle de pedido existente",
		description = "Actualiza los datos de un detalle de pedido si existe"
	)
	@ApiResponse(responseCode = "200", description = "Detalle de pedido modificado correctamente",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = DetallePedido.class)))
	@ApiResponse(responseCode = "404", description = "Detalle de pedido no encontrado")
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

	@Operation(
		summary = "Eliminar un detalle de pedido por ID",
		description = "Elimina un detalle de pedido específico del sistema"
	)
	@ApiResponse(responseCode = "200", description = "Detalle de pedido eliminado correctamente",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = DetallePedido.class)))
	@ApiResponse(responseCode = "404", description = "Detalle de pedido no encontrado")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Long id) {
		Optional<DetallePedido> eliminado = service.delete(id);
		if (eliminado.isPresent()) {
			return ResponseEntity.ok(eliminado.get());
		}
		return ResponseEntity.notFound().build();
	}
}
