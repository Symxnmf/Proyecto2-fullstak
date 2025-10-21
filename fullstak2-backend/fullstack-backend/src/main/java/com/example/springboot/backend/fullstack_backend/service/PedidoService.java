package com.example.springboot.backend.fullstack_backend.service;

import java.util.List;
import java.util.Optional;

import com.example.springboot.backend.fullstack_backend.entity.Pedido;



public interface PedidoService {

    List<Pedido> findByAll();

    Optional<Pedido> findById(Long id);

    Pedido save(Pedido unPedido);

    Optional<Pedido> delete(Long id);


}