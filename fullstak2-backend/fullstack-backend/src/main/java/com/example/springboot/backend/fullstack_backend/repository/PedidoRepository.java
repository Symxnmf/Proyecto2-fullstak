package com.example.springboot.backend.fullstack_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.springboot.backend.fullstack_backend.entity.Pedido;


public interface PedidoRepository extends CrudRepository<Pedido, Long> {
	Optional<Pedido> findTopByOrderByNumeroDesc();
	List<Pedido> findByUsuarioIgnoreCaseOrderByIdDesc(String usuario);
}