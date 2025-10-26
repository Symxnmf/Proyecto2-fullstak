package com.example.springboot.backend.fullstack_backend.restcontrollers;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
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

import com.example.springboot.backend.fullstack_backend.controllers.ProductoRestController;
import com.example.springboot.backend.fullstack_backend.entity.Producto;
import com.example.springboot.backend.fullstack_backend.service.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = ProductoRestController.class)
class ProductoRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductoService productoService;

    @TestConfiguration
    static class MockitoConfig {
        @Bean
        ProductoService productoService() {
            return org.mockito.Mockito.mock(ProductoService.class);
        }
    }

    private Producto buildProducto(Long id) {
        Producto p = new Producto();
        p.setId(id);
        p.setNombre("Laptop Gamer");
        p.setPrecio(1500);
        p.setStock(10);
        p.setDescripcion("Laptop potente para juegos");
        p.setCategoria("Tecnologia");
        p.setImagen("laptop.jpg");
        p.setOferta(Boolean.TRUE);
        return p;
    }

    @Test
    @DisplayName("GET /api/productos - debe listar productos")
    void listarProductos_ok() throws Exception {
        List<Producto> lista = Arrays.asList(buildProducto(1L), buildProducto(2L));
        BDDMockito.given(productoService.findByAll()).willReturn(lista);

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("Laptop Gamer")));
    }

    @Test
    @DisplayName("GET /api/productos/{id} - producto existente")
    void verProducto_existente_ok() throws Exception {
        Producto prod = buildProducto(1L);
        BDDMockito.given(productoService.findById(1L)).willReturn(Optional.of(prod));

        mockMvc.perform(get("/api/productos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Laptop Gamer")))
                .andExpect(jsonPath("$.oferta", is(true)));
    }

    @Test
    @DisplayName("GET /api/productos/{id} - producto no existe retorna 404")
    void verProducto_noExiste_404() throws Exception {
        BDDMockito.given(productoService.findById(99L)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/productos/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/productos - crear producto válido -> 201")
    void crearProducto_valido_created() throws Exception {
        Producto toCreate = buildProducto(null);
        Producto saved = buildProducto(1L);
        BDDMockito.given(productoService.save(any(Producto.class))).willReturn(saved);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Laptop Gamer")));
    }

    @Test
    @DisplayName("POST /api/productos - crear producto inválido -> 400")
    void crearProducto_invalido_badRequest() throws Exception {
        // nombre en blanco y oferta nula violan @NotBlank y @NotNull
        Producto invalido = new Producto();
        invalido.setNombre("");
        invalido.setPrecio(-1);
        invalido.setStock(-5);
        invalido.setCategoria("");
        invalido.setOferta(null);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/productos/{id} - modificar existente -> 200")
    void modificarProducto_existente_ok() throws Exception {
        Producto existente = buildProducto(1L);
        Producto actualizado = buildProducto(1L);
        actualizado.setNombre("Laptop Actualizada");

        BDDMockito.given(productoService.findById(1L)).willReturn(Optional.of(existente));
        BDDMockito.given(productoService.save(any(Producto.class))).willReturn(actualizado);

        mockMvc.perform(put("/api/productos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Laptop Actualizada")));
    }

    @Test
    @DisplayName("PUT /api/productos/{id} - modificar no existente -> 404")
    void modificarProducto_noExiste_404() throws Exception {
        Producto payload = buildProducto(1L);
        BDDMockito.given(productoService.findById(1L)).willReturn(Optional.empty());

        mockMvc.perform(put("/api/productos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/productos/{id} - eliminar existente -> 204")
    void eliminarProducto_existente_noContent() throws Exception {
        Producto existente = buildProducto(1L);
        BDDMockito.given(productoService.findById(1L)).willReturn(Optional.of(existente));
        BDDMockito.given(productoService.delete(1L)).willReturn(Optional.of(existente));

        mockMvc.perform(delete("/api/productos/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/productos/{id} - eliminar no existente -> 404")
    void eliminarProducto_noExiste_404() throws Exception {
        BDDMockito.given(productoService.findById(123L)).willReturn(Optional.empty());

        mockMvc.perform(delete("/api/productos/{id}", 123L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/productos/ofertas - lista ofertas -> 200")
    void listarOfertas_ok() throws Exception {
        List<Producto> ofertas = Arrays.asList(buildProducto(1L));
        BDDMockito.given(productoService.findOfertas()).willReturn(ofertas);

        mockMvc.perform(get("/api/productos/ofertas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].oferta", is(true)));
    }

    @Test
    @DisplayName("GET /api/productos/categorias - contar por categoria -> 200")
    void contarCategorias_ok() throws Exception {
        Object[] row1 = new Object[] { "Tecnologia", 5 };
        Object[] row2 = new Object[] { "Hogar", 3 };
        BDDMockito.given(productoService.contarPorCategoria()).willReturn(Arrays.asList(row1, row2));

        mockMvc.perform(get("/api/productos/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre", is("Tecnologia")))
                .andExpect(jsonPath("$[0].cantidad", is(5)))
                .andExpect(jsonPath("$[1].nombre", is("Hogar")))
                .andExpect(jsonPath("$[1].cantidad", is(3)));
    }

    @Test
    @DisplayName("POST /api/productos/batch - crear varios -> 201")
    void crearProductosBatch_created() throws Exception {
        List<Producto> payload = Arrays.asList(buildProducto(null), buildProducto(null));
        List<Producto> saved = Arrays.asList(buildProducto(1L), buildProducto(2L));

        // Simular guardado secuencial en el controller (se llama save por cada producto)
        BDDMockito.given(productoService.save(any(Producto.class)))
                .willReturn(saved.get(0), saved.get(1));

        mockMvc.perform(post("/api/productos/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }
}
