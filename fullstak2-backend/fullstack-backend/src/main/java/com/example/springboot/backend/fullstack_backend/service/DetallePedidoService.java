package com.example.springboot.backend.fullstack_backend.service;

import java.util.List;
import java.util.Optional;

import com.example.springboot.backend.fullstack_backend.entity.DetallePedido;


public interface DetallePedidoService {

    List<DetallePedido> findByAll();

    Optional<DetallePedido> findById(Long id);

    DetallePedido save(DetallePedido unDetallePedido);

    Optional<DetallePedido> delete(Long id);


}