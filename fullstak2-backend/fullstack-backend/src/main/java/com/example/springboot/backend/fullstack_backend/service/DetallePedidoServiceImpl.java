package com.example.springboot.backend.fullstack_backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springboot.backend.fullstack_backend.entity.DetallePedido;
import com.example.springboot.backend.fullstack_backend.repository.DetallePedidoRepository;


@Service
public class DetallePedidoServiceImpl implements DetallePedidoService {

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DetallePedido> findByAll() {
        return (List<DetallePedido>) detallePedidoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DetallePedido> findById(Long id) {
        return detallePedidoRepository.findById(id);
    }

    @Override
    @Transactional
    public DetallePedido save(DetallePedido unDetallePedido) {
        return detallePedidoRepository.save(unDetallePedido);
    }

    @Override
    @Transactional
    public Optional<DetallePedido> delete(Long id) {
    Optional<DetallePedido> detallePedidoOptional = detallePedidoRepository.findById(id);
    detallePedidoOptional.ifPresent(detallePedido -> detallePedidoRepository.deleteById(id));
    return detallePedidoOptional;
}


}