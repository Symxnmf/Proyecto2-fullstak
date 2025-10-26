package com.example.springboot.backend.fullstack_backend.restcontrollers;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.springboot.backend.fullstack_backend.controllers.RolRestControllers;
import com.example.springboot.backend.fullstack_backend.entity.Rol;
import com.example.springboot.backend.fullstack_backend.service.RolService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(RolRestControllers.class)
public class RolRestControllersTest {

	@Autowired
	private MockMvc mockmvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private RolService rolService;

	@Test
	public void verRolesTest() throws Exception {
		when(rolService.findByAll()).thenReturn(List.of());
		mockmvc.perform(get("/api/roles").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	public void verUnRolTest() {
		Rol r = new Rol("cliente");
		r.setId(1L);
		try {
			when(rolService.findById(1L)).thenReturn(Optional.of(r));
			mockmvc.perform(get("/api/roles/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
		} catch (Exception ex) {
			fail("El testing lanzó un error: " + ex.getMessage());
		}
	}

	@Test
	public void rolNoExisteTest() throws Exception {
		when(rolService.findById(10L)).thenReturn(Optional.empty());
		mockmvc.perform(get("/api/roles/10").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}

	@Test
	public void crearRolTest() throws Exception {
		Rol in = new Rol("nuevo");
		Rol out = new Rol("nuevo");
		out.setId(3L);
		when(rolService.save(any(Rol.class))).thenReturn(out);

		mockmvc.perform(post("/api/roles").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(in))).andExpect(status().isCreated());
	}
}