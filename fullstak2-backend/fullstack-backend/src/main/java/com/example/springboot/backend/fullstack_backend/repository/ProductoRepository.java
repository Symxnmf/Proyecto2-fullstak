package com.example.springboot.backend.fullstack_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.example.springboot.backend.fullstack_backend.entity.Producto;


public interface ProductoRepository extends CrudRepository<Producto, Long> {
	List<Producto> findByOfertaTrue();

	List<Producto> findByNombreContainingIgnoreCase(String nombre);

	@Query("select p.categoria as categoria, count(p) as cantidad from Producto p group by p.categoria")
	List<Object[]> contarPorCategoria();
}