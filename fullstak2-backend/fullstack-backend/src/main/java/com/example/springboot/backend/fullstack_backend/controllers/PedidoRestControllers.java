package com.example.springboot.backend.fullstack_backend.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.backend.fullstack_backend.entity.Pedido;
import com.example.springboot.backend.fullstack_backend.entity.DetallePedido;
import com.example.springboot.backend.fullstack_backend.entity.Producto;
import com.example.springboot.backend.fullstack_backend.repository.ProductoRepository;
import com.example.springboot.backend.fullstack_backend.repository.DetallePedidoRepository;
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

	@Autowired
	private ProductoRepository productoRepository;

	@Autowired
	private DetallePedidoRepository detallePedidoRepository;

	@Operation(summary = "Obtener lista de pedidos", description = "Devuelve todos los registros de pedidos")
	@ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida",
		content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Pedido.class))))
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
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
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Pedido> crear(@RequestBody Pedido unPedido) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.save(unPedido));
	}

	@Operation(summary = "Modificar un pedido existente", description = "Actualiza los datos de un pedido si existe")
	@ApiResponse(responseCode = "200", description = "Pedido modificado correctamente",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = Pedido.class)))
	@ApiResponse(responseCode = "404", description = "Pedido no encontrado")
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
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
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> eliminar(@PathVariable Long id) {
		Optional<Pedido> pedidoOptional = service.delete(id);
		if (pedidoOptional.isPresent()) {
			return ResponseEntity.ok(pedidoOptional.get());
		}
		return ResponseEntity.notFound().build();
	}

	@Operation(summary = "Mis pedidos", description = "Obtiene los pedidos del usuario autenticado")
	@ApiResponse(responseCode = "200", description = "Pedidos del usuario",
		content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Pedido.class))))
	@GetMapping("/mios")
	public List<Pedido> misPedidos(Authentication auth) {
		String email = auth.getName();
		return service.findByUsuario(email);
	}

	@Operation(summary = "Checkout (genera boleta)", description = "Crea un pedido calculando subtotal, IVA y total; asigna número correlativo")
	@ApiResponse(responseCode = "201", description = "Pedido creado con boleta",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = Pedido.class)))
	@PostMapping("/checkout")
	public ResponseEntity<?> checkout(Authentication auth, @RequestBody Map<String, Object> body) {
		try {
			String email = auth != null ? auth.getName() : null;
			if (email == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "No autenticado"));
			}

			double ivaRate = 0.19;
			double subtotal = 0.0;
			double total = 0.0;

			Object itemsObj = body.get("items");
			java.util.List<?> items = (itemsObj instanceof java.util.List) ? (java.util.List<?>) itemsObj : java.util.List.of();
			
			java.util.List<com.example.springboot.backend.fullstack_backend.entity.DetallePedido> detallesList = new java.util.ArrayList<>();
			if (!items.isEmpty()) {
				for (Object it : items) {
					if (it instanceof java.util.Map<?, ?>) {
						@SuppressWarnings("unchecked")
						java.util.Map<String, Object> map = (java.util.Map<String, Object>) it;
						Long pid = Long.valueOf(String.valueOf(map.get("productoId")));
						Object qtyObj = map.get("cantidad");
						int cantidad = qtyObj == null ? 1 : Integer.parseInt(String.valueOf(qtyObj));
						
						Optional<Producto> prodOpt = productoRepository.findById(pid);
						if (prodOpt.isEmpty()) continue;
						Producto prod = prodOpt.get();
						double line = prod.getPrecio() * Math.max(cantidad, 1);
						subtotal += line;
						
						DetallePedido det = new DetallePedido();
						det.setProducto(prod);
						det.setCantidad(Math.max(cantidad, 1));
						det.setSubtotal(line);
						detallesList.add(det);
					}
				}
			}

			if (subtotal <= 0) {
				subtotal = ((Number) body.getOrDefault("subtotal", 0)).doubleValue();
				total = ((Number) body.getOrDefault("total", 0)).doubleValue();
				System.out.println("No hay items, usando valores enviados - subtotal: " + subtotal + ", total: " + total);
			}

			double iva;
			if (subtotal > 0) {
				iva = Math.round(subtotal * ivaRate * 100.0) / 100.0;
				total = Math.round((subtotal + iva) * 100.0) / 100.0;
			} else if (total > 0) {
				subtotal = Math.round((total / (1 + ivaRate)) * 100.0) / 100.0;
				iva = Math.round((total - subtotal) * 100.0) / 100.0;
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "items o subtotal/total requerido"));
			}

			Pedido p = new Pedido();
			p.setUsuario(email);
			p.setFecha(String.valueOf(java.time.LocalDateTime.now()));
			p.setSubtotal(subtotal);
			p.setIva(iva);
			p.setTotal(total);
			Object detalles = body.get("detalles");
			p.setDetalles(detalles != null ? detalles.toString() : null);
			Long numero = service.getNextNumeroBoleta().orElse(1L);
			p.setNumero(numero);
			
			Pedido saved = service.save(p);
			
			if (!detallesList.isEmpty()) {
				for (DetallePedido d : detallesList) {
					d.setPedido(saved);
					detallePedidoRepository.save(d);
				}
			}
			
			return ResponseEntity.status(HttpStatus.CREATED).body(saved);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
		}
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public List<Pedido> adminList(
			@RequestParam(required = false) String cliente,
			@RequestParam(required = false) String desde,
			@RequestParam(required = false) String hasta
	) {
		List<Pedido> base = service.findByAll();
		java.time.LocalDateTime from = desde != null ? java.time.LocalDateTime.parse(desde) : null;
		java.time.LocalDateTime to = hasta != null ? java.time.LocalDateTime.parse(hasta) : null;
		return base.stream()
			.filter(p -> cliente == null || p.getUsuario() != null && p.getUsuario().equalsIgnoreCase(cliente))
			.filter(p -> {
				if (from == null && to == null) return true;
				try {
					java.time.LocalDateTime f = java.time.LocalDateTime.parse(p.getFecha());
					boolean ok = true;
					if (from != null) ok &= !f.isBefore(from);
					if (to != null) ok &= !f.isAfter(to);
					return ok;
				} catch (Exception e) { return true; }
			})
			.sorted((a,b) -> Long.compare(b.getId(), a.getId()))
			.toList();
	}
}
