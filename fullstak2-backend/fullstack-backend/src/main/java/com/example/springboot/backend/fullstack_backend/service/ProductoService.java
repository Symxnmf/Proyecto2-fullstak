package com.example.springboot.backend.fullstack_backend.service;

import java.util.List;
import java.util.Optional;

import com.example.springboot.backend.fullstack_backend.entity.Producto;



public interface ProductoService {

    List<Producto> findByAll();

    Optional<Producto> findById(Long id);

    Producto save(Producto unProducto);

    Optional<Producto> delete(Long id);

    List<Producto> findOfertas();

    List<Object[]> contarPorCategoria();


}