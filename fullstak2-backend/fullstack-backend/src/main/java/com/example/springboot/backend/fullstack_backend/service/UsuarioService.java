package com.example.springboot.backend.fullstack_backend.service;

import java.util.List;
import java.util.Optional;

import com.example.springboot.backend.fullstack_backend.entity.Usuario;


public interface UsuarioService {
    List<Usuario> findByAll();

    Optional<Usuario> findById(Long id);

    Usuario save(Usuario unUsuario);

    Optional<Usuario> delete (Usuario unUsuario);
}