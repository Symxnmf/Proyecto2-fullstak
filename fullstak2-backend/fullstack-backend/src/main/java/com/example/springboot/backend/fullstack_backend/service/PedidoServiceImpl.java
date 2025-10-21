package com.example.springboot.backend.fullstack_backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springboot.backend.fullstack_backend.entity.Pedido;
import com.example.springboot.backend.fullstack_backend.repository.PedidoRepository;


@Service
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> findByAll() {
        return (List<Pedido>) pedidoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pedido> findById(Long id) {
        return pedidoRepository.findById(id);
    }

    @Override
    @Transactional
    public Pedido save(Pedido unPedido) {
        return pedidoRepository.save(unPedido);
    }

    @Override
    @Transactional
    public Optional<Pedido> delete(Long id) {
    Optional<Pedido> pedidoOptional = pedidoRepository.findById(id);
    pedidoOptional.ifPresent(pedido -> pedidoRepository.deleteById(id));
    return pedidoOptional;
}


}