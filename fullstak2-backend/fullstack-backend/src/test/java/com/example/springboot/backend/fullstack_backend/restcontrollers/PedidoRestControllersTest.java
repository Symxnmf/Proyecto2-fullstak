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

import com.example.springboot.backend.fullstack_backend.controllers.PedidoRestControllers;
import com.example.springboot.backend.fullstack_backend.entity.Pedido;
import com.example.springboot.backend.fullstack_backend.service.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = PedidoRestControllers.class)
class PedidoRestControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PedidoService pedidoService;

    @TestConfiguration
    static class MockitoConfig {
        @Bean
        PedidoService pedidoService() {
            return org.mockito.Mockito.mock(PedidoService.class);
        }
    }

    private Pedido buildPedido(Long id) {
        Pedido p = new Pedido();
        p.setId(id);
        p.setFecha("2025-10-24");
        p.setTotal(100.5);
        p.setUsuario("juan@example.com");
        p.setDetalles("2 items");
        return p;
    }

    @Test
    @DisplayName("GET /api/pedidos - listar pedidos")
    void listarPedidos_ok() throws Exception {
        List<Pedido> lista = Arrays.asList(buildPedido(1L), buildPedido(2L));
        BDDMockito.given(pedidoService.findByAll()).willReturn(lista);

        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    @DisplayName("GET /api/pedidos/{id} - existente -> 200")
    void verPedido_existente_ok() throws Exception {
        Pedido p = buildPedido(1L);
        BDDMockito.given(pedidoService.findById(1L)).willReturn(Optional.of(p));

        mockMvc.perform(get("/api/pedidos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.usuario", is("juan@example.com")));
    }

    @Test
    @DisplayName("GET /api/pedidos/{id} - no existe -> 404")
    void verPedido_noExiste_404() throws Exception {
        BDDMockito.given(pedidoService.findById(999L)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/pedidos/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/pedidos - crear -> 201")
    void crearPedido_created() throws Exception {
        Pedido toCreate = buildPedido(null);
        Pedido saved = buildPedido(1L);
        BDDMockito.given(pedidoService.save(org.mockito.ArgumentMatchers.any(Pedido.class))).willReturn(saved);

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("PUT /api/pedidos/{id} - modificar existente -> 200")
    void modificarPedido_existente_ok() throws Exception {
        Pedido existente = buildPedido(1L);
        Pedido payload = buildPedido(1L);
        payload.setDetalles("3 items");

        BDDMockito.given(pedidoService.findById(1L)).willReturn(Optional.of(existente));
        BDDMockito.given(pedidoService.save(org.mockito.ArgumentMatchers.any(Pedido.class)))
                .willReturn(payload);

        mockMvc.perform(put("/api/pedidos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detalles", is("3 items")));
    }

    @Test
    @DisplayName("PUT /api/pedidos/{id} - modificar no existente -> 404")
    void modificarPedido_noExiste_404() throws Exception {
        Pedido payload = buildPedido(1L);
        BDDMockito.given(pedidoService.findById(1L)).willReturn(Optional.empty());

        mockMvc.perform(put("/api/pedidos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/pedidos/{id} - eliminar existente -> 200")
    void eliminarPedido_existente_ok() throws Exception {
        Pedido p = buildPedido(1L);
        BDDMockito.given(pedidoService.delete(1L)).willReturn(Optional.of(p));

        mockMvc.perform(delete("/api/pedidos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("DELETE /api/pedidos/{id} - eliminar no existente -> 404")
    void eliminarPedido_noExiste_404() throws Exception {
        BDDMockito.given(pedidoService.delete(999L)).willReturn(Optional.empty());

        mockMvc.perform(delete("/api/pedidos/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}
