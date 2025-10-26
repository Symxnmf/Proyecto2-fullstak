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

import com.example.springboot.backend.fullstack_backend.controllers.UsuarioRestControllers;
import com.example.springboot.backend.fullstack_backend.entity.Usuario;
import com.example.springboot.backend.fullstack_backend.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = UsuarioRestControllers.class)
class UsuarioRestControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioService usuarioService;

    @TestConfiguration
    static class MockitoConfig {
        @Bean
        UsuarioService usuarioService() {
            return org.mockito.Mockito.mock(UsuarioService.class);
        }
    }

    private Usuario buildUsuario(Long id) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setNombre("Juan");
        u.setApellido("Perez");
        u.setCorreo("juan@example.com");
        u.setCelular("123456789");
        return u;
    }

    @Test
    @DisplayName("GET /api/usuarios - listar usuarios")
    void listarUsuarios_ok() throws Exception {
        List<Usuario> lista = Arrays.asList(buildUsuario(1L), buildUsuario(2L));
        BDDMockito.given(usuarioService.findByAll()).willReturn(lista);

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("Juan")));
    }

    @Test
    @DisplayName("GET /api/usuarios/{id} - existente -> 200")
    void verUsuario_existente_ok() throws Exception {
        Usuario u = buildUsuario(1L);
        BDDMockito.given(usuarioService.findById(1L)).willReturn(Optional.of(u));

        mockMvc.perform(get("/api/usuarios/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.correo", is("juan@example.com")));
    }

    @Test
    @DisplayName("GET /api/usuarios/{id} - no existe -> 404")
    void verUsuario_noExiste_404() throws Exception {
        BDDMockito.given(usuarioService.findById(999L)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/usuarios/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/usuarios - crear -> 201")
    void crearUsuario_created() throws Exception {
        Usuario toCreate = buildUsuario(null);
        Usuario saved = buildUsuario(1L);
        BDDMockito.given(usuarioService.save(org.mockito.ArgumentMatchers.any(Usuario.class))).willReturn(saved);

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("PUT /api/usuarios/{id} - modificar existente -> 200")
    void modificarUsuario_existente_ok() throws Exception {
        Usuario existente = buildUsuario(1L);
        Usuario payload = buildUsuario(1L);
        payload.setNombre("Carlos");

        BDDMockito.given(usuarioService.findById(1L)).willReturn(Optional.of(existente));
        BDDMockito.given(usuarioService.save(org.mockito.ArgumentMatchers.any(Usuario.class)))
                .willReturn(payload);

        mockMvc.perform(put("/api/usuarios/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Carlos")));
    }

    @Test
    @DisplayName("PUT /api/usuarios/{id} - modificar no existente -> 404")
    void modificarUsuario_noExiste_404() throws Exception {
        Usuario payload = buildUsuario(1L);
        BDDMockito.given(usuarioService.findById(1L)).willReturn(Optional.empty());

        mockMvc.perform(put("/api/usuarios/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/usuarios/{id} - eliminar existente -> 200")
    void eliminarUsuario_existente_ok() throws Exception {
        Usuario existente = buildUsuario(1L);
        BDDMockito.given(usuarioService.delete(org.mockito.ArgumentMatchers.any(Usuario.class)))
                .willReturn(Optional.of(existente));

        mockMvc.perform(delete("/api/usuarios/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("DELETE /api/usuarios/{id} - eliminar no existente -> 404")
    void eliminarUsuario_noExiste_404() throws Exception {
        BDDMockito.given(usuarioService.delete(org.mockito.ArgumentMatchers.any(Usuario.class)))
                .willReturn(Optional.empty());

        mockMvc.perform(delete("/api/usuarios/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}
