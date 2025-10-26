package com.example.springboot.backend.fullstack_backend.service;

import java.util.List;
import java.util.Optional;

import com.example.springboot.backend.fullstack_backend.entity.Rol;


public interface RolService {   

    List<Rol> findByAll();

    Optional<Rol> findById(Long id);

    Rol save(Rol unRol);

    Optional<Rol> delete(Rol unRol);

    Optional<Rol> delete(Long id);

    Optional<Rol> findByNombre(String nombre);

}