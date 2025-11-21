package com.example.springboot.backend.fullstack_backend.repository;

import com.example.springboot.backend.fullstack_backend.entity.CarritoItem;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CarritoItemRepository extends CrudRepository<CarritoItem, Long> {
    List<CarritoItem> findByUsuarioIgnoreCase(String usuario);
    void deleteByUsuarioIgnoreCase(String usuario);
}
