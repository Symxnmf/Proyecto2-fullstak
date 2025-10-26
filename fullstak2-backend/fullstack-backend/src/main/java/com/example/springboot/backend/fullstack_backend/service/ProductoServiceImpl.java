package com.example.springboot.backend.fullstack_backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springboot.backend.fullstack_backend.entity.Producto;
import com.example.springboot.backend.fullstack_backend.repository.ProductoRepository;


@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productorepositories;

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findByAll() {
        return (List<Producto>) productorepositories.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Producto> findById(Long id) {
        return productorepositories.findById(id);
    }

    @Override
    @Transactional
    public Producto save(Producto unProducto) {
        return productorepositories.save(unProducto);
    }

    @Override
    @Transactional
    public Optional<Producto> delete(Long id) {
    Optional<Producto> productoOptional = productorepositories.findById(id);
    productoOptional.ifPresent(producto -> productorepositories.deleteById(id));
    return productoOptional;
}

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findOfertas() {
        return productorepositories.findByOfertaTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscarPorNombre(String nombre) {
        return productorepositories.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> contarPorCategoria() {
        return productorepositories.contarPorCategoria();
    }


}