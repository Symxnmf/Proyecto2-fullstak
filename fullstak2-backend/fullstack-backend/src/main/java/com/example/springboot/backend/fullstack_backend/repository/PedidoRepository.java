package com.example.springboot.backend.fullstack_backend.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.springboot.backend.fullstack_backend.entity.Pedido;


public interface PedidoRepository extends CrudRepository<Pedido, Long> {

}