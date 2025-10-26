package com.example.springboot.backend.fullstack_backend.restcontrollers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.springboot.backend.fullstack_backend.controllers.DetallePedidoRestControllers;
import com.example.springboot.backend.fullstack_backend.entity.DetallePedido;
import com.example.springboot.backend.fullstack_backend.entity.Pedido;
import com.example.springboot.backend.fullstack_backend.entity.Producto;
import com.example.springboot.backend.fullstack_backend.service.DetallePedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = DetallePedidoRestControllers.class)
class DetallePedidoRestControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DetallePedidoService detallePedidoService;

    @TestConfiguration
    static class MockitoConfig {
        @Bean
        DetallePedidoService detallePedidoService() {
            return org.mockito.Mockito.mock(DetallePedidoService.class);
        }
    }

    private DetallePedido buildDetalle(Long id) {
        Producto prod = new Producto();
        prod.setId(10L);
        prod.setNombre("Mouse");
        prod.setPrecio(20);
        prod.setStock(100);
        prod.setCategoria("Tecnologia");
        prod.setOferta(Boolean.FALSE);

        Pedido ped = new Pedido();
        ped.setId(5L);
        ped.setUsuario("juan@example.com");
        ped.setFecha("2025-10-24");
        ped.setTotal(40.0);
        ped.setDetalles("1 item");

        DetallePedido d = new DetallePedido();
        d.setId(id);
        d.setCantidad(2);
        d.setSubtotal(40.0);
        d.setProducto(prod);
        d.setPedido(ped);
        return d;
    }

    @Test
    @DisplayName("GET /api/detallepedidos - listar detalles")
    void listarDetalles_ok() throws Exception {
        List<DetallePedido> lista = Arrays.asList(buildDetalle(1L), buildDetalle(2L));
        BDDMockito.given(detallePedidoService.findByAll()).willReturn(lista);

        mockMvc.perform(get("/api/detallepedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    @DisplayName("GET /api/detallepedidos/{id} - existente -> 200")
    void verDetalle_existente_ok() throws Exception {
        DetallePedido d = buildDetalle(1L);
        BDDMockito.given(detallePedidoService.findById(1L)).willReturn(Optional.of(d));

        mockMvc.perform(get("/api/detallepedidos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.cantidad", is(2)));
    }

    @Test
    @DisplayName("GET /api/detallepedidos/{id} - no existe -> 404")
    void verDetalle_noExiste_404() throws Exception {
        BDDMockito.given(detallePedidoService.findById(999L)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/detallepedidos/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/detallepedidos - crear -> 201")
    void crearDetalle_created() throws Exception {
        DetallePedido toCreate = buildDetalle(null);
        DetallePedido saved = buildDetalle(1L);
        BDDMockito.given(detallePedidoService.save(org.mockito.ArgumentMatchers.any(DetallePedido.class)))
                .willReturn(saved);

        mockMvc.perform(post("/api/detallepedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("PUT /api/detallepedidos/{id} - modificar existente -> 200")
    void modificarDetalle_existente_ok() throws Exception {
        DetallePedido existente = buildDetalle(1L);
        DetallePedido payload = buildDetalle(1L);
        payload.setCantidad(3);
        payload.setSubtotal(60.0);

        BDDMockito.given(detallePedidoService.findById(1L)).willReturn(Optional.of(existente));
        BDDMockito.given(detallePedidoService.save(org.mockito.ArgumentMatchers.any(DetallePedido.class)))
                .willReturn(payload);

        mockMvc.perform(put("/api/detallepedidos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidad", is(3)))
                .andExpect(jsonPath("$.subtotal", is(60.0)));
    }

    @Test
    @DisplayName("PUT /api/detallepedidos/{id} - modificar no existente -> 404")
    void modificarDetalle_noExiste_404() throws Exception {
        DetallePedido payload = buildDetalle(1L);
        BDDMockito.given(detallePedidoService.findById(1L)).willReturn(Optional.empty());

        mockMvc.perform(put("/api/detallepedidos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/detallepedidos/{id} - eliminar existente -> 200")
    void eliminarDetalle_existente_ok() throws Exception {
        DetallePedido d = buildDetalle(1L);
        BDDMockito.given(detallePedidoService.delete(1L)).willReturn(Optional.of(d));

        mockMvc.perform(delete("/api/detallepedidos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("DELETE /api/detallepedidos/{id} - eliminar no existente -> 404")
    void eliminarDetalle_noExiste_404() throws Exception {
        BDDMockito.given(detallePedidoService.delete(999L)).willReturn(Optional.empty());

        mockMvc.perform(delete("/api/detallepedidos/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}
