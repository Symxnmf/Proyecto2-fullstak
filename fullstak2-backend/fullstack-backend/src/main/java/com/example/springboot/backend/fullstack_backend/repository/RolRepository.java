package com.example.springboot.backend.fullstack_backend.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.springboot.backend.fullstack_backend.entity.Rol;

public interface RolRepository extends CrudRepository<Rol, Long> {

	java.util.Optional<Rol> findByNombre(String nombre);

}