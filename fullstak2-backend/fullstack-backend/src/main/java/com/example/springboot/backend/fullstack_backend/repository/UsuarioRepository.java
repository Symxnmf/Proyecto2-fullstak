package com.example.springboot.backend.fullstack_backend.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.springboot.backend.fullstack_backend.entity.Usuario;


public interface UsuarioRepository extends CrudRepository <Usuario, Long>{

}