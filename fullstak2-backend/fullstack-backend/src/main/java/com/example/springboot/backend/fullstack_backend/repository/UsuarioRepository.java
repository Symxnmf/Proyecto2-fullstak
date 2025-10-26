package com.example.springboot.backend.fullstack_backend.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.springboot.backend.fullstack_backend.entity.Usuario;


public interface UsuarioRepository extends CrudRepository <Usuario, Long>{
    Optional<Usuario> findByCorreo(String correo);
}