package com.example.springboot.backend.fullstack_backend.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springboot.backend.fullstack_backend.entity.Rol;
import com.example.springboot.backend.fullstack_backend.repository.RolRepository;


@Service
public class RolServiceImpl implements RolService {

    @Autowired
    private RolRepository rolRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Rol> findByAll() {
        return (List<Rol>) rolRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Rol> findById(Long id) {
        return rolRepository.findById(id);
    }

    @Override
    @Transactional
    public Rol save(Rol unRol) {
        return rolRepository.save(unRol);
    }

    @Transactional
    public Optional<Rol> delete(Long id) {
    Optional<Rol> rolOptional = rolRepository.findById(id);
    rolOptional.ifPresent(rol -> rolRepository.deleteById(id));
    return rolOptional;
}

    @Override
    public Optional<Rol> delete(Rol unRol) {
        Optional<Rol> rolOptional = rolRepository.findById(unRol.getId());
        rolOptional.ifPresent(r -> rolRepository.delete(unRol));
        return rolOptional;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Rol> findByNombre(String nombre) {
        return rolRepository.findByNombre(nombre);
    }

}